package com.example.batch.job;

import com.example.batch.common.enums.DeliveryStatus;
import com.example.batch.common.enums.InventoryStatus;
import com.example.batch.common.enums.OrdersStatus;
import com.example.batch.common.exception.BaseException;
import com.example.batch.common.model.BaseResponseStatus;
import com.example.batch.domain.delivery.DeliveryRepository;
import com.example.batch.domain.delivery.model.Delivery;
import com.example.batch.domain.head.HeadIncomeRepository;
import com.example.batch.domain.head.HeadInventoryRepository;
import com.example.batch.domain.head.model.HeadIncome;
import com.example.batch.domain.head.model.HeadInventory;
import com.example.batch.domain.inventory.InventoryMovementRepository;
import com.example.batch.domain.inventory.model.InventoryMovement;
import com.example.batch.domain.orders.OrdersRepository;
import com.example.batch.domain.orders.model.Orders;
import com.example.batch.domain.orders.model.OrdersItem;
import com.example.batch.domain.store.StoreInventoryRepository;
import com.example.batch.domain.store.model.StoreInventory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderApproveWriter implements ItemWriter<Long> {

    private final OrdersRepository ordersRepository;
    private final HeadInventoryRepository headInventoryRepository;
    private final HeadIncomeRepository headIncomeRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    public void write(Chunk<? extends Long> chunk) {
        for (Long orderId : chunk) {
            try {
                processOrder(orderId);
            } catch (BaseException e) {
            }
        }
    }

    private void processOrder(Long orderId) {
        Orders orders = ordersRepository.findByIdWithDetailsForUpdate(orderId)
                .filter(o -> o.getOrdersStatus() == OrdersStatus.CONFIRMED)
                .orElse(null);

        if (orders == null) {
            return;
        }

        applyOutbound(orders);
        orders.approve();
        saveHeadIncome(orders);
        startDelivery(orders);
    }

    private void applyOutbound(Orders orders) {
        Long storeIdx = orders.getStore().getIdx();
        String memo = "발주 일괄승인 ordersIdx=" + orders.getIdx();

        for (OrdersItem item : orders.getOrdersItemList()) {
            Long productIdx = item.getProduct().getIdx();

            HeadInventory headInventory = headInventoryRepository.findByProductIdxForUpdate(productIdx)
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.HEAD_INVENTORY_NOT_FOUND));

            if (headInventory.getCount() < item.getCount()) {
                throw new BaseException(BaseResponseStatus.ORDERS_APPROVE_INSUFFICIENT_STOCK);
            }

            headInventory.setCount(headInventory.getCount() - item.getCount());
            headInventory.setStatus(resolveInventoryStatus(headInventory.getCount(), item.getProduct().getMinStock()));

            storeInventoryRepository.save(new StoreInventory(
                    null,
                    item.getCount(),
                    InventoryStatus.NORMAL,
                    headInventory.getManufacturedDate(),
                    item.getCount(),
                    orders.getStore(),
                    item.getProduct()
            ));

            inventoryMovementRepository.save(
                    InventoryMovement.transferOut(item.getProduct(), storeIdx, item.getCount(), memo)
            );
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
        Delivery delivery = deliveryRepository.findByOrders(orders);
        if (delivery == null || delivery.getDeliveryStatus() != DeliveryStatus.READY) {
            return;
        }
        delivery.setDeliveryStatus(DeliveryStatus.START);
    }

    private InventoryStatus resolveInventoryStatus(int count, int minStock) {
        if (count <= 0) return InventoryStatus.CRITICAL;
        if (count <= minStock / 2) return InventoryStatus.CRITICAL;
        if (count <= minStock) return InventoryStatus.LOW;
        return InventoryStatus.NORMAL;
    }
}
