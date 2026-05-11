package com.example.nexus.domain.report;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.domain.pos.PosOrdersItemRepository;
import com.example.nexus.domain.pos.PosPayRepository;
import com.example.nexus.domain.report.model.Report;
import com.example.nexus.domain.report.model.ReportDto;
import com.example.nexus.domain.user.UserRepository;
import com.example.nexus.domain.user.model.User;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.example.nexus.common.model.BaseResponseStatus.NOT_FOUND_USER;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final PosPayRepository posPayRepository;
    private final PosOrdersItemRepository posOrdersItemRepository;
    private final UserRepository userRepository;

    private final ChatClient chatClient;
    private final S3Client s3Client;

    @Value("classpath:aiReport/prompts/report-template.md")
    private Resource reportTemplate;

    @Value("${spring.cloud.aws.s3.report-bucket}")
    private String reportBucket;

    // 0. 생성자 주입
    public ReportService(ReportRepository reportRepository,
                         PosPayRepository posPayRepository,
                         PosOrdersItemRepository posOrdersItemRepository,
                         UserRepository userRepository,
                         ChatClient.Builder chatClientBuilder,
                         S3Client s3Client) {
        this.reportRepository = reportRepository;
        this.posPayRepository = posPayRepository;
        this.posOrdersItemRepository = posOrdersItemRepository;
        this.userRepository = userRepository;
        this.s3Client = s3Client;
        this.chatClient = chatClientBuilder.build();
    }

    // 1. 메인 로직 (트랜잭션 분리 및 안전한 예외 처리)
    public String createAndSaveReport(Long userIdx, String userMessage) {
        User loginUser = userRepository.findById(userIdx)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        // AI 답변 받기
        String aiResponse = handleChatbotRequest(userMessage);

        if (aiResponse.contains("[CHAT]")) {
            return aiResponse.replace("[CHAT]", "").trim();
        }

        // 제목 추출 로직
        String title = "AI 분석 보고서";
        if (aiResponse.contains("[TITLE:")) {
            int start = aiResponse.indexOf("[TITLE:") + 7;
            int end = aiResponse.indexOf("]", start);
            if (end != -1) {
                title = aiResponse.substring(start, end).trim();
            }
        }

        String aiMarkdown = aiResponse.replaceAll("\\[REPORT\\]|\\[TITLE:.*?\\]", "").trim();

        // 임시 PDF 파일 생성
        File tempFile = generatePdfFromAiResponse(aiMarkdown);

        try {
            // S3 업로드 및 DB 저장을 수동 롤백 로직으로 안전하게 처리
            return saveReportResult(loginUser, title, tempFile);
        } catch (Exception e) {
            throw new RuntimeException("보고서 생성 및 저장 실패: " + e.getMessage(), e);
        } finally {
            // 디스크 용량 확보를 위해 임시 파일은 무조건 삭제
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    // 2. DB 저장 및 S3 업로드 (수동 롤백 방식 - 트랜잭션 에러 완벽 해결)
    public String saveReportResult(User user, String title, File tempFile) {
        String s3Key = "reports/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                + "/" + UUID.randomUUID() + ".pdf";

        // 1. S3에 PDF 파일 먼저 업로드
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(reportBucket).key(s3Key).contentType("application/pdf").build(),
                RequestBody.fromFile(tempFile));

        // 2. DB 저장 시도 및 수동 롤백
        try {
            reportRepository.save(Report.builder().title(title).filePath(s3Key).user(user).build());
            return title + " 보고서 생성이 완료되었습니다!";
        } catch (Exception e) {
            // DB 저장 실패 시: S3에 방금 올린 파일 즉시 삭제 (롤백)
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(reportBucket).key(s3Key).build());
            throw new RuntimeException("DB 저장 중 오류가 발생하여 S3 업로드 파일을 삭제(롤백)했습니다.", e);
        }
    }

    // 3. AI에게 프롬프트 전송
    public String handleChatbotRequest(String userMessage) {
        ReportDto.ReportDataSummaryDto summary = getRecentSummary();

        try {
            String template = reportTemplate.getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
            List<String> top5 = summary.topProducts();

            String finalInstruction = template
                    .replace("{{totalSales}}", String.format("%,d", summary.totalSales()))
                    .replace("{{product1}}", top5.size() > 0 ? top5.get(0) : "데이터 없음")
                    .replace("{{product2}}", top5.size() > 1 ? top5.get(1) : "데이터 없음")
                    .replace("{{product3}}", top5.size() > 2 ? top5.get(2) : "데이터 없음")
                    .replace("{{product4}}", top5.size() > 3 ? top5.get(3) : "데이터 없음")
                    .replace("{{product5}}", top5.size() > 4 ? top5.get(4) : "데이터 없음");

            String finalPrompt = finalInstruction + "\n\n사용자의 질문: " + userMessage;

            return chatClient.prompt()
                    .user(finalPrompt)
                    .call()
                    .content();

        } catch (java.io.IOException e) {
            throw new RuntimeException("보고서 템플릿 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // 4. DB 수치 집계 (최근 30일)
    public ReportDto.ReportDataSummaryDto getRecentSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate  = now.minusDays(30);

        Long totalSales = posPayRepository.sumSalesByDateBetween(startDate, now);
        if (totalSales == null) totalSales = 0L;

        List<String> topProducts = posOrdersItemRepository.findTopSellingMenus(startDate, now, PageRequest.of(0, 5));

        return ReportDto.ReportDataSummaryDto.builder()
                .totalSales(totalSales)
                .topProducts(topProducts)
                .build();
    }

    // 5. 마크다운 -> HTML -> PDF 변환 (JAR 폰트 호환성 및 나눔고딕 적용)
    private File generatePdfFromAiResponse(String aiMarkdown) {
        try {
            String htmlContent = convertMarkdownToHtml(aiMarkdown);

            // 보고서 전용 나눔고딕 CSS 적용
            String fullHtml = "<html><head><style>" +
                    "body { font-family: 'NanumGothic', sans-serif; font-size: 12px; line-height: 1.6; padding: 30px; color: #333; }" +
                    "h1 { font-size: 18px; color: #F37321; border-bottom: 2px solid #F37321; padding-bottom: 8px; margin-bottom: 20px; }" +
                    "h2 { font-size: 14px; color: #222; margin-top: 25px; margin-bottom: 10px; border-bottom: 1px solid #eee; padding-bottom: 5px; }" +
                    "h3 { font-size: 13px; color: #444; margin-top: 15px; margin-bottom: 5px; }" +
                    "p, li { font-size: 12px; color: #555; }" +
                    "blockquote { background: #f9f9f9; border-left: 4px solid #F37321; margin: 10px 0; padding: 12px 15px; color: #666; font-weight: bold; }" +
                    "table { width: 100%; border-collapse: collapse; margin-top: 10px; margin-bottom: 20px; }" +
                    "th, td { border: 1px solid #ddd; padding: 10px; text-align: left; font-size: 11px; }" +
                    "th { background-color: #F37321; color: white; font-weight: bold; }" +
                    "ul { margin-top: 5px; padding-left: 20px; }" +
                    "</style></head><body>" +
                    htmlContent +
                    "</body></html>";

            // 시스템 임시 폴더 사용 (서버 배포 시 권한 문제 방지)
            String tempDir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tempDir, "temp_report_" + UUID.randomUUID() + ".pdf");

            // 리소스 폰트 로드 (나눔고딕 경로 유지)
            Resource fontResource = new ClassPathResource("aiReport/fonts/NanumGothic.ttf");

            try (OutputStream os = new FileOutputStream(tempFile)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(fullHtml, null);

                // JAR 패키징 대응: File 객체 대신 Stream 방식으로 폰트 주입
                builder.useFont(() -> {
                    try {
                        return fontResource.getInputStream();
                    } catch (Exception ex) {
                        throw new RuntimeException("폰트 파일을 읽을 수 없습니다.", ex);
                    }
                }, "NanumGothic"); // 🌟 나눔고딕으로 변경

                builder.toStream(os);
                builder.run();
            }

            return tempFile;

        } catch (Exception e) {
            throw new RuntimeException("임시 PDF 파일 생성 중 오류 발생: " + e.getMessage(), e);
        }
    }

    // 6. 마크다운 변환기
    private String convertMarkdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

    // 7. 사용자별 보고서 리스트 조회
    @Transactional(readOnly = true)
    public ReportDto.ReportPageRes reportList(Long userIdx, int page, int size) {
        User user = userRepository.findById(userIdx)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Report> result = reportRepository.findByUserOrderByCreatedAtDesc(user, pageRequest);

        return ReportDto.ReportPageRes.from(result);
    }
}