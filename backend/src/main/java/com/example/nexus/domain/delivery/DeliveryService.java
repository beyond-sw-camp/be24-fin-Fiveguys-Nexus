package com.example.nexus.domain.delivery;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.common.enums.NotificationType;
import com.example.nexus.domain.delivery.model.Delivery;
import com.example.nexus.domain.delivery.model.DeliveryDto;
import com.example.nexus.domain.notification.HeadNotificationService;
import com.example.nexus.domain.notification.StoreNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nexus.domain.orders.model.Orders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final HeadNotificationService headNotificationService;
    private final StoreNotificationService storeNotificationService;

    // 발주 승인 시 배송 생성 (READY 상태)
    @Transactional
    public void createDelivery(Orders orders) {
        Delivery delivery = Delivery.builder()
                .deliveryStatus(DeliveryStatus.READY)
                .departureDate(LocalDateTime.now())
                .estimatedArrivalAt(LocalDateTime.now().plusDays(1))
                .orders(orders)
                .build();
        deliveryRepository.save(delivery);
    }

    // 배송 승인: READY → START + 점주 배송 시작 알림 (NOTIFY_014)
    @Transactional
    public boolean approveDelivery(Long deliveryIdx) {
        Delivery delivery = deliveryRepository.findByIdx(deliveryIdx);
        if (delivery == null || delivery.getDeliveryStatus() != DeliveryStatus.READY) {
            return false;
        }

        delivery.setDeliveryStatus(DeliveryStatus.START);

        String storeName = delivery.getOrders().getStore().getStoreName();
        storeNotificationService.create(
                NotificationType.DELIVERY_START,
                "배송 시작 안내",
                "주문하신 상품의 배송이 시작되었습니다. 예상 도착일: "
                        + delivery.getEstimatedArrivalAt().toLocalDate(),
                delivery.getOrders().getStore());

        return true;
    }

    public List<DeliveryDto> getDeliveriesByHead(
            String storeName, DeliveryStatus status, Integer year, Integer month, Integer day) {
        return deliveryRepository.findAllByFilters(storeName, status, year, month, day)
                .stream()
                .map(DeliveryDto::from)
                .collect(Collectors.toList());
    }

    // 가맹점 배송 현황 조회
    public List<DeliveryDto> getDeliveriesForStore(
            Long storeIdx, Long orderIdx, DeliveryStatus status, Integer year, Integer month, Integer day) {
        return deliveryRepository.findAllByStoreFilters(storeIdx, orderIdx, status, year, month, day)
                .stream()
                .map(delivery -> DeliveryDto.from(delivery))
                .collect(Collectors.toList());
    }

    // 본사 배송 지연 사유 입력 로직 (throw/Optional 제거 및 boolean 흐름 제어 적용)
    @Transactional
    public boolean updateDelayReason(Long deliveryIdx, String delayReason) {
        Delivery delivery = deliveryRepository.findByIdx(deliveryIdx);

        if (delivery == null) {
            return false;
        }

        // 상태를 지연으로 변경
        delivery.setDelayReason(delayReason);
        delivery.setDeliveryStatus(DeliveryStatus.DELAY);

        // 배송 지연 알림 발송 (본사)
        String delayStoreName = delivery.getOrders().getStore().getStoreName();
        headNotificationService.create(
                NotificationType.DELIVERY_DELAY,
                "배송 지연 - " + delayStoreName,
                delayStoreName + " 배송이 지연되었습니다. 사유: " + delayReason);

        // 배송 지연 알림 발송 (가맹점, NOTIFY_016)
        storeNotificationService.create(
                NotificationType.DELIVERY_DELAY,
                "배송 지연 안내",
                "배송이 지연되었습니다. 사유: " + delayReason,
                delivery.getOrders().getStore());

        return true;
    }
}
