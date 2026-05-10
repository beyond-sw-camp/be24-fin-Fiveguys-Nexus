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
import org.springframework.core.io.ClassPathResource;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.example.nexus.common.model.BaseResponseStatus.*;

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

    /**
     * 메인 로직: 데이터 준비 → AI 분석 → PDF 생성 → S3 업로드 → DB 저장
     *
     * 1. createAndSaveReport (메인 공정 시작)
     * 2. handleChatbotRequest (AI에게 질문 던지기)
     * 3. getRecentSummary (DB에서 분석용 수치 데이터 추출)
     * 4. generatePdfFromAiResponse (AI 답변을 PDF 파일로 굽기)
     * 5. convertMarkdownToHtml (텍스트 형식을 웹 형식으로 변환)
     * S3 업로드 및 DB 저장 (최종 보관)
     */

    // 0. AI에게 성격(프롬포트)를 고정해 두기 위해서 생성자를 직접 생성
    public ReportService(ReportRepository reportRepository,
                         PosPayRepository posPayRepository,
                         PosOrdersItemRepository posOrdersItemRepository,
                         UserRepository userRepository,
                         ChatClient.Builder chatClientBuilder,
                         S3Client s3Client
    ) {
        this.reportRepository = reportRepository;
        this.posPayRepository = posPayRepository;
        this.posOrdersItemRepository = posOrdersItemRepository;
        this.userRepository = userRepository;

        this.s3Client = s3Client;
        this.chatClient = chatClientBuilder.build();
    }

    // 1, 회원 메세지와 우저 정보를 받아와 S3와 DB에 저장
    @Transactional
    public String createAndSaveReport(Long userIdx, String userMessage) {
        // 회원 존재 여부 확인
        User loginUser = userRepository.findById(userIdx)
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        // AI 답변 받기
        String aiResponse = handleChatbotRequest(userMessage);

        // 상황 A: 일상 대화 ([CHAT]) 이름표를 달고 왔을 때
        if (aiResponse.contains("[CHAT]")) {
            // 기존의 PDF 생성, S3 업로드 로직을 전부 건너뛰고 텍스트만 바로 반환!
            return aiResponse.replace("[CHAT]", "").trim();
        }

        // [제목 추출 로직 추가]
        String title = "AI 분석 보고서"; // 기본값
        if (aiResponse.contains("[TITLE:")) {
            int start = aiResponse.indexOf("[TITLE:") + 7;
            int end = aiResponse.indexOf("]", start);
            if (end != -1) {
                title = aiResponse.substring(start, end);
            }
        }

        // AI 분석 및 임시 PDF 생성 (기존 로직)
        String aiMarkdown = aiResponse.replaceAll("\\[REPORT\\]|\\[TITLE:.*?\\]", "").trim();
        String localFilePath = generatePdfFromAiResponse(aiMarkdown);
        File tempFile = new File(localFilePath);

        // S3 업로드 경로(Key) 생성
        String s3Key = "reports/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                + "/" + UUID.randomUUID() + ".pdf";

        try {
            // S3로 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(reportBucket)
                    .key(s3Key)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile));

            // 트랜잭션 동기화 매니저에 롤백 시 작업을 예약함
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    // 트랜잭션이 STATUS_ROLLED_BACK(롤백) 상태로 끝났다면 S3 파일 삭제
                    if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                        s3Client.deleteObject(DeleteObjectRequest.builder()
                                .bucket(reportBucket)
                                .key(s3Key)
                                .build());
                    }
                }
            });

            // 6. DB 저장
            Report report = Report.builder()
                    .title(title)
                    .filePath(s3Key)
                    .user(loginUser) // 연관관계 매핑
                    .build();

            reportRepository.save(report);

            return title + "보고서 생성이 완료되었습니다! 보고서 목록에서 확인해주세요.";

        } catch (Exception e) {
            throw new RuntimeException("보고서 생성 실패: " + e.getMessage(), e);
        } finally {
            // 서버에 남은 임시 PDF 삭제
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    //   2. AI에게 질문 던지기
    public String handleChatbotRequest(String userMessage) {
        // 최신 데이터 집계 가져오기
        ReportDto.ReportDataSummaryDto summary = getRecentSummary();

        try {
            // 🌟 1. 외부 마크다운 파일(report-template.md) 읽어오기
            String template = reportTemplate.getContentAsString(java.nio.charset.StandardCharsets.UTF_8);

            // 🌟 2. 템플릿의 변수들({{...}})을 실제 데이터로 치환
            List<String> top5 = summary.topProducts();
            String finalInstruction = template
                    .replace("{{totalSales}}", String.format("%,d", summary.totalSales()))
                    .replace("{{product1}}", top5.size() > 0 ? top5.get(0) : "데이터 없음")
                    .replace("{{product2}}", top5.size() > 1 ? top5.get(1) : "데이터 없음")
                    .replace("{{product3}}", top5.size() > 2 ? top5.get(2) : "데이터 없음")
                    .replace("{{product4}}", top5.size() > 3 ? top5.get(3) : "데이터 없음")
                    .replace("{{product5}}", top5.size() > 4 ? top5.get(4) : "데이터 없음");

            // 4. AI에게 보낼 최종 프롬프트 구성
            String finalPrompt = finalInstruction + "\n\n사용자의 질문: " + userMessage;

            // 5. 프롬프트 전송 및 답변 반환
            return chatClient.prompt()
                    .user(finalPrompt)
                    .call()
                    .content();

        } catch (java.io.IOException e) {
            throw new RuntimeException("보고서 템플릿 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    // 3. DB에서 데이터 가지고 오기
    public ReportDto.ReportDataSummaryDto getRecentSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate  = now.minusDays(30); // 최근 30일 기준

        Long totalSales = posPayRepository.sumSalesByDateBetween(startDate, now);

        // 매출이 아예 없는 경우 null이 반환될 수 있으므로 0으로 안전하게 처리
        if (totalSales == null) {
            totalSales = 0L;
        }

        // PageRequest.of(0, 5)을 통해 LIMIT 5과 동일하게 5개만 가져옴
        List<String> topProducts = posOrdersItemRepository.findTopSellingMenus(startDate, now, PageRequest.of(0, 5));

        return ReportDto.ReportDataSummaryDto.builder()
                .totalSales(totalSales)
                .topProducts(topProducts)
                .build();
    }

    // 4. HTML을 PDF로 만들고 서버에 저장하는 메서드
    private String generatePdfFromAiResponse(String aiMarkdown) {
        try {
            String htmlContent = convertMarkdownToHtml(aiMarkdown);

            // 🌟 마법의 CSS (폰트 크기 축소, 여백 정렬, 테이블/인용구 디자인)
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
                    // 기존에 강제로 넣던 <h2> 제목과 <hr/>은 마크다운과 겹치므로 삭제했습니다!
                    htmlContent +
                    "</body></html>";

            String directoryPath = "uploads/temp_reports/";
            Path path = Paths.get(directoryPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String fileName = "temp_report_" + UUID.randomUUID().toString() + ".pdf";
            String fullFilePath = directoryPath + fileName;

            File fontFile = new ClassPathResource("aiReport/fonts/NanumGothic.ttf").getFile();

            try (OutputStream os = new FileOutputStream(fullFilePath)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(fullHtml, null);
                builder.useFont(fontFile, "NanumGothic");
                builder.toStream(os);
                builder.run();
            }

            return fullFilePath;

        } catch (Exception e) {
            throw new RuntimeException("임시 PDF 파일 생성 중 오류 발생", e);
        }
    }

    // 5. 마크다운 텍스트를 HTML로 변환하는 메서드
    private String convertMarkdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }


    // 보고서 조회
    @Transactional(readOnly = true)
    public ReportDto.ReportPageRes reportList(Long userIdx, int page, int size) {
        // 사용자 존재 및 삭제 여부 확인 (보안 검증)
        User user = userRepository.findById(userIdx)
                .filter(u -> !u.isDeleted()) // 삭제되지 않은 사용자인지 확인
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Report> result = reportRepository.findByUserOrderByCreatedAtDesc(user, pageRequest);

        return ReportDto.ReportPageRes.from(result);
    }
}
