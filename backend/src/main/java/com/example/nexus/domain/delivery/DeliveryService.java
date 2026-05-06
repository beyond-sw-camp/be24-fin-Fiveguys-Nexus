package com.example.nexus.domain.delivery;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.common.enums.NotificationType;
import com.example.nexus.domain.delivery.model.Delivery;
import com.example.nexus.domain.delivery.model.DeliveryDto;
import com.example.nexus.domain.notification.HeadNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final HeadNotificationService headNotificationService;

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
        // Optional을 사용하지 않고 직접 Entity를 조회하여 null 체크
        Delivery delivery = deliveryRepository.findByIdx(deliveryIdx);

        if (delivery == null) {
            return false;
        }

        // 상태를 지연으로 변경
        delivery.setDelayReason(delayReason);
        delivery.setDeliveryStatus(DeliveryStatus.DELAY);

        // 배송 지연 알림 발송
        String delayStoreName = delivery.getOrders().getStore().getStoreName();
        headNotificationService.create(
                NotificationType.DELIVERY_DELAY,
                "배송 지연 - " + delayStoreName,
                delayStoreName + " 배송이 지연되었습니다. 사유: " + delayReason);

        return true;
    }
}
