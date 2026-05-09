package com.example.nexus.domain.report.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

public class ReportDto {

    // 사용자 요청 DTO
    public record ChatRequest(
            @NotBlank(message = "메시지는 비어있을 수 없습니다.")
            String message
    ) {}

    // DB에서 데이터를 가져올때 사용하는 DTO
    @Builder
    public record ReportDataSummaryDto(
            Long totalSales,          // 최근 30일 총 매출액
            List<String> topProducts  // 인기 상품 TOP 3
    ) {}
}
