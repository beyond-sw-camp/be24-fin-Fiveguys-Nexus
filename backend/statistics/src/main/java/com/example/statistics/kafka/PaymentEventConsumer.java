package com.example.statistics.kafka;

import com.example.statistics.common.enums.PosPayMethod;
import com.example.statistics.domain.menu.MenuRepository;
import com.example.statistics.domain.pos.PosOrdersItemRepository;
import com.example.statistics.domain.pos.PosPayRepository;
import com.example.statistics.domain.pos.model.PosOrdersItem;
import com.example.statistics.domain.pos.model.PosPay;
import com.example.statistics.domain.store.StoreRepository;
import com.example.statistics.event.KafkaTopics;
import com.example.statistics.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PosPayRepository posPayRepository;
    private final PosOrdersItemRepository posOrdersItemRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;

    @KafkaListener(topics = KafkaTopics.POS_PAYMENT_CREATED, concurrency = "3")
    @Transactional
    public void onPaymentCreated(PaymentEvent event) {
        log.info("[pos.payment.created] received: posPayIdx={}, storeIdx={}, payAmount={}, items={}",
                event.posPayIdx(), event.storeIdx(), event.payAmount(), event.items().size());

        // 멱등성: 같은 결제 중복 수신 시 skip
        if (posPayRepository.existsById(event.posPayIdx())) {
            log.warn("[pos.payment.created] duplicate payment skipped: posPayIdx={}", event.posPayIdx());
            return;
        }

        // 1) PosPay 저장 (이벤트의 idx 그대로)
        PosPay posPay = PosPay.builder()
                .idx(event.posPayIdx())
                .method(PosPayMethod.valueOf(event.method()))
                .paidAt(event.paidAt())
                .payAmount(event.payAmount())
                .store(storeRepository.getReferenceById(event.storeIdx()))   // FK proxy
                .build();
        posPayRepository.save(posPay);

        // 2) PosOrdersItem 각각 저장
        List<PosOrdersItem> orderItems = event.items().stream()
                .map(item -> PosOrdersItem.builder()
                        .menu(menuRepository.getReferenceById(item.menuIdx()))   // FK proxy
                        .posPay(posPay)
                        .quantity(item.quantity())
                        .build())
                .toList();
        posOrdersItemRepository.saveAll(orderItems);
    }
}