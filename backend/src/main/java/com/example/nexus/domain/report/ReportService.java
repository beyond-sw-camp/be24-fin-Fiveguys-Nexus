package com.example.nexus.domain.report;

import com.example.nexus.domain.pos.PosOrdersItemRepository;
import com.example.nexus.domain.pos.PosPayRepository;
import com.example.nexus.domain.report.model.ReportDto;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final PosPayRepository posPayRepository;
    private final PosOrdersItemRepository posOrdersItemRepository;
    private final ChatClient chatClient;

    // AI에게 성격(프롬포트)를 고정해 두기 위해서 생성자를 직접 생성
    public ReportService(ReportRepository reportRepository,
                         PosPayRepository posPayRepository,
                         PosOrdersItemRepository posOrdersItemRepository,
                         ChatClient.Builder chatClientBuilder,
                         @Value("${report.ai.system-prompt}") String systemPrompt) {
        this.reportRepository = reportRepository;
        this.posPayRepository = posPayRepository;
        this.posOrdersItemRepository = posOrdersItemRepository;

        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
                .build();
    }

    //  사용자 요청 및 응답
    public String handleChatbotRequest(String userMessage) {
        // 1. 최신 데이터 집계 가져오기
        ReportDto.ReportDataSummaryDto summary = getRecentSummary();

        // 2. 데이터가 없을 경우를 대비한 방어 로직 (환각 방지)
        String topProductsText = summary.topProducts().isEmpty()
                ? "현재 판매 데이터가 없습니다."
                : String.join(", ", summary.topProducts());


        String dataContext = String.format(
                "[시스템 데이터]\n- 최근 30일 기준 총 매출: %d원\n- 인기 상품 TOP 3: %s\n\n",
                summary.totalSales(),
                topProductsText
        );

        // 3. 데이터와 사용자 질문을 합쳐서 AI에게 전송
        String finalPrompt = dataContext + "사용자의 질문: " + userMessage;

        return chatClient.prompt()
                .user(finalPrompt)
                .call()
                .content();
    }

    // DB에서 데이터 가지고 오기
    public ReportDto.ReportDataSummaryDto getRecentSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate  = now.minusDays(30); // 최근 30일 기준

        Long totalSales = posPayRepository.sumSalesByDateBetween(startDate, now);

        // 매출이 아예 없는 경우 null이 반환될 수 있으므로 0으로 안전하게 처리
        if (totalSales == null) {
            totalSales = 0L;
        }

        // PageRequest.of(0, 3)을 통해 LIMIT 3과 동일하게 3개만 가져옴
        List<String> topProducts = posOrdersItemRepository.findTopSellingMenus(startDate, now, PageRequest.of(0, 3));

        return ReportDto.ReportDataSummaryDto.builder()
                .totalSales(totalSales)
                .topProducts(topProducts)
                .build();
    }

}
