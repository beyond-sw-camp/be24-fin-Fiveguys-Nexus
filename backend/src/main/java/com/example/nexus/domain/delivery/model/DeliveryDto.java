package com.example.nexus.domain.delivery.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

    private Long deliveryIdx;
    private Long orderIdx;
    private String storeName;
    private String deliveryStatus;
    private String delayReason;
    private LocalDateTime departureDate;
    private LocalDateTime estimatedArrivalAt;
    private LocalDateTime deliveredDate;

    public static DeliveryDto from(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryIdx(delivery.getIdx())
                .orderIdx(delivery.getOrders().getIdx())
                .storeName(delivery.getOrders().getStore().getStoreName())
                .deliveryStatus(delivery.getDeliveryStatus().name())
                .delayReason(delivery.getDelayReason())
                .departureDate(delivery.getDepartureDate())
                .estimatedArrivalAt(delivery.getEstimatedArrivalAt())
                .deliveredDate(delivery.getDeliveredDate())
                .build();
    }

    // 본사 지연 사유 입력시 Request Body로 받을 정적 내부 클래스 추가
    @Getter
    @NoArgsConstructor
    public static class DelayReasonRequest {
        private Long deliveryIdx;
        private String delayReason;
    }
}