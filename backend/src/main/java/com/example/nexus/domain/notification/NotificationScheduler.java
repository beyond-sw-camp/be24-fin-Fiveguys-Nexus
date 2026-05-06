package com.example.nexus.domain.notification;

import com.example.nexus.common.enums.NotificationType;
import com.example.nexus.domain.head.HeadInventoryRepository;
import com.example.nexus.domain.head.model.HeadInventory;
import com.example.nexus.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final HeadInventoryRepository headInventoryRepository;
    private final HeadNotificationService headNotificationService;
    private final HeadNotificationRepository headNotificationRepository;

    /**
     * NOTIFY_002: 재고 부족 알림
     * 매일 오전 7시에 실행
     * 본사 재고 수량이 제품의 최소 기준(minStock) 이하이면 알림 발송
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void checkLowStockNotification() {
        LocalDateTime now = LocalDateTime.now();

        // 재고가 있는 모든 본사 재고 조회
        List<HeadInventory> inventories = headInventoryRepository.findAllByCountGreaterThan(0);

        for (HeadInventory inventory : inventories) {
            Product product = inventory.getProduct();

            // 현재 재고 수량이 제품의 최소 재고 기준 이하인지 확인
            if (inventory.getCount() <= product.getMinStock()) {
                String title = "재고 부족 - " + product.getProductName();
                String content = "현재 수량: " + inventory.getCount() + product.getProductUnit()
                        + " (최소 기준: " + product.getMinStock() + product.getProductUnit() + ")";

                // 같은 제품에 대해 오늘 이미 알림을 보냈으면 중복 발송하지 않음
                boolean exists = headNotificationRepository.existsByTypeAndTitleAndCreatedAtAfter(
                        NotificationType.LOW_STOCK, title, now.toLocalDate().atStartOfDay());
                if (!exists) {
                    headNotificationService.create(NotificationType.LOW_STOCK, title, content);
                }
            }
        }
    }

    /**
     * NOTIFY_001: 유통기한 임박 알림
     * 매일 오전 7시에 실행
     * 제조일 + dangerDays 기준으로 유통기한 7일 이내 제품 감지
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void checkExpiryNotification() {
        // 유통기한 만료까지 며칠 이내일 때 알림을 보낼지 설정
        int alertDays = 7;
        LocalDateTime now = LocalDateTime.now();

        // 재고 수량이 1 이상인 제품만 조회 (재고 없으면 의미 없으므로)
        List<HeadInventory> inventories = headInventoryRepository.findAllByCountGreaterThan(0);

        for (HeadInventory inventory : inventories) {
            // 제품의 유통기한 일수 (예: "30" → 30일)
            int shelfLifeDays = Integer.parseInt(inventory.getProduct().getDangerDays());

            // 유통기한 만료일 = 제조일 + 유통기한 일수
            LocalDateTime expiryDate = inventory.getManufacturedDate().plusDays(shelfLifeDays);

            // 만료일까지 남은 일수 계산
            long daysUntilExpiry = java.time.Duration.between(now, expiryDate).toDays();

            // 만료일이 7일 이내이고, 아직 만료되지 않은 경우에만 알림
            if (daysUntilExpiry <= alertDays && daysUntilExpiry >= 0) {
                String productName = inventory.getProduct().getProductName();
                String title = "유통기한 임박 - " + productName;
                String content = "유통기한 " + daysUntilExpiry + "일 이내입니다. (만료일: "
                        + expiryDate.toLocalDate() + ")";

                // 같은 제품에 대해 오늘 이미 알림을 보냈으면 중복 발송하지 않음
                boolean exists = headNotificationRepository.existsByTypeAndTitleAndCreatedAtAfter(
                        NotificationType.EXPIRY, title, now.toLocalDate().atStartOfDay());
                if (!exists) {
                    headNotificationService.create(NotificationType.EXPIRY, title, content);
                }
            }
        }
    }
}
