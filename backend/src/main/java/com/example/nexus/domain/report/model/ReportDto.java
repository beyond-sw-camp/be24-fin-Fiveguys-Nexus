package com.example.nexus.domain.report.model;

import com.example.nexus.domain.store.model.StoreDto;
import com.example.nexus.domain.user.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
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

    @Getter
    @Builder
    public static class ReportList{
        private Long idx;
        private String reportTitle;
        private String reportFilePath;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;

        public static ReportDto.ReportList from(Report entity){
            return ReportList.builder()
                    .idx(entity.getIdx())
                    .reportTitle(entity.getReportTitle())
                    .reportFilePath(entity.getReportFilePath())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }

    // 페이지 조회
    @Getter
    @Builder
    public static class ReportPageRes{
        private List<ReportDto.ReportList> reportList;
        private int totalPage;
        private long totalCount;
        private int currentPage;
        private int currentSize;

        public static ReportDto.ReportPageRes from(Page<Report> entity){
            return ReportPageRes.builder()
                    .reportList(entity.map(ReportList::from).getContent())
                    .totalPage(entity.getTotalPages())
                    .totalCount(entity.getTotalElements())
                    .currentPage(entity.getNumber())
                    .currentSize(entity.getSize())
                    .build();
        }
    }
}