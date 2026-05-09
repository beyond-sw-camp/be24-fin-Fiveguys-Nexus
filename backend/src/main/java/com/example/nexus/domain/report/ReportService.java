package com.example.nexus.domain.report;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final ChatClient chatClient;

    // AI에게 성격(프롬포트)를 고정해 두기 위해서 생성자를 직접 생성
    public ReportService(ReportRepository reportRepository, ChatClient.Builder chatClientBuilder) {
        this.reportRepository = reportRepository;

        this.chatClient = chatClientBuilder
                .defaultSystem("당신은 프랜차이즈 본사의 데이터 분석 전문가입니다. " +
                        "전달받은 수치 데이터를 바탕으로 전문적인 마크다운 형식의 보고서를 작성합니다. " +
                        "친절하지만 간결한 비즈니스 톤을 유지하세요.")
                .build();
    }

    public String handleChatbotRequest(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
}
