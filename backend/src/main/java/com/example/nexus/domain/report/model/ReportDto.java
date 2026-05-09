package com.example.nexus.domain.report.model;

import jakarta.validation.constraints.NotBlank;

public class ReportDto {

    public record ChatRequest(
            @NotBlank(message = "메시지는 비어있을 수 없습니다.")
            String message
    ) {}
}
