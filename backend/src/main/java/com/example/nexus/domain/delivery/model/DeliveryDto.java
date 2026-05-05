package com.example.nexus.domain.delivery.model;

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
                .storeName(delivery.getOrders().getStore().getStoreName())
                .deliveryStatus(delivery.getDeliveryStatus().name())
                .delayReason(delivery.getDelayReason())
                .departureDate(delivery.getDepartureDate())
                .estimatedArrivalAt(delivery.getEstimatedArrivalAt())
                .deliveredDate(delivery.getDeliveredDate())
                .build();
    }
}
