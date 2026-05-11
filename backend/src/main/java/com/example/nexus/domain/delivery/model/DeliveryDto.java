package com.example.nexus.domain.delivery.model;

import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

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

    // 페이징 응답 DTO
    @Getter
    @Builder
    public static class DeliveryPageRes {

        private List<DeliveryDto> deliveryList;
        private int totalPage;
        private long totalCount;
        private int currentPage;
        private int currentSize;

        public static DeliveryPageRes from(Page<Delivery> page) {

            return DeliveryPageRes.builder()
                    .deliveryList(
                            page.getContent()
                                    .stream()
                                    .map(DeliveryDto::from)
                                    .toList()
                    )
                    .totalPage(page.getTotalPages())
                    .totalCount(page.getTotalElements())
                    .currentPage(page.getNumber())
                    .currentSize(page.getSize())
                    .build();
        }
    }

    // 배송 지연 사유 요청 DTO
    @Getter
    @NoArgsConstructor
    public static class DelayReasonRequest {

        private Long deliveryIdx;
        private String delayReason;
    }
}