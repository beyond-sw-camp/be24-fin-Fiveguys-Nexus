package com.example.nexus.domain.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public String handleChatbotRequest(String userMessage) {
        // TODO: 1. 사용자의 질문(userMessage)을 분석해서 필요한 데이터 기간/매장을 파악한다.
        // TODO: 2. DB에서 해당 매출/재고 데이터를 조회해온다.
        // TODO: 3. 데이터를 프롬프트에 담아 AI에게 보고서 생성을 요청한다.
        // TODO: 4. AI가 준 답변을 파일로 만들고 ReportRepository에 저장한다.

        // 일단 프론트와 잘 연결되는지 확인하기 위한 임시 응답
        return "요청하신 [" + userMessage + "]에 대한 보고서 생성을 시작합니다. 완료되면 보고서 목록에서 확인해주세요!";
    }
}
