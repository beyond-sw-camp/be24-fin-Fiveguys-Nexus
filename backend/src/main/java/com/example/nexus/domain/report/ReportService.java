package com.example.nexus.domain.report;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final ChatClient chatClient;

    // AI에게 성격(프롬포트)를 고정해 두기 위해서 생성자를 직접 생성
    public ReportService(ReportRepository reportRepository,
                         ChatClient.Builder chatClientBuilder,
                         @Value("${report.ai.system-prompt}") String systemPrompt) {
        this.reportRepository = reportRepository;

        this.chatClient = chatClientBuilder
                .defaultSystem(systemPrompt)
                .build();
    }

    public String handleChatbotRequest(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
}
