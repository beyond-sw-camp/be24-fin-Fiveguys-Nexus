package com.example.batch.job;

import com.example.batch.common.enums.DeliveryStatus;
import com.example.batch.domain.delivery.DeliveryRepository;
import com.example.batch.domain.delivery.model.Delivery;
import com.example.batch.domain.head.HeadIncomeRepository;
import com.example.batch.domain.head.model.HeadIncome;
import com.example.batch.domain.orders.model.Orders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderApproveStep2Writer implements ItemWriter<Orders> {

    private final HeadIncomeRepository headIncomeRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    public void write(Chunk<? extends Orders> chunk) {
        for (Orders orders : chunk) {
            log.info("approving orderId={}", orders.getIdx());
            orders.approve();
            saveHeadIncome(orders);
            startDelivery(orders);
        }
    }

    private void saveHeadIncome(Orders orders) {
        HeadIncome headIncome = new HeadIncome();
        headIncome.setPrice(orders.getPrice());
        headIncome.setStatus(false);
        headIncome.setSettlementIdx(null);
        headIncome.setStore(orders.getStore());
        headIncome.setOrders(orders);
        headIncomeRepository.save(headIncome);
    }

    private void startDelivery(Orders orders) {
        Delivery delivery = deliveryRepository.findByOrdersForUpdate(orders);
        if (delivery == null || delivery.getDeliveryStatus() != DeliveryStatus.READY) {
            return;
        }
        delivery.setDeliveryStatus(DeliveryStatus.START);
    }
}
