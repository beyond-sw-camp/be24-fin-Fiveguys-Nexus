package com.example.nexus.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    // AI 챗봇 대화 히스토리 저장소
    // 초기 단계: 인메모리(InMemoryChatMemory) — 서버 재시작 시 소실, 단일 인스턴스용
    // 운영 단계에서 JDBC/Redis 기반으로 교체할 수 있도록 빈으로 분리
    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }
}
