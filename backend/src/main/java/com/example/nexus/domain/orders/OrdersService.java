package com.example.nexus.domain.orders;

import com.example.nexus.common.enums.OrdersStatus;
import com.example.nexus.common.enums.OrdersType;
import com.example.nexus.common.exception.BaseException;
import com.example.nexus.common.model.BaseResponseStatus;
import com.example.nexus.domain.orders.model.*;
import com.example.nexus.domain.product.ProductRepository;
import com.example.nexus.domain.product.model.Product;
import com.example.nexus.domain.store.StoreRepository;
import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.user.model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdersService {
    private final DangerRepository dangerRepository;
    private final OrdersRepository ordersRepository;
    private final OrdersItemRepository ordersItemRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public List<OrdersDto.OrdersRes> findAllAuto() {
        return ordersRepository.findAllByOrdersTypeAndOrdersStatus(OrdersType.AUTO, OrdersStatus.WAITING).stream()
                .map(OrdersDto.OrdersRes::from)
                .toList();
    }

    public List<OrdersDto.OrdersRes> findAllManual() {
        return ordersRepository.findAllByOrdersTypeAndOrdersStatus(OrdersType.MANUAL, OrdersStatus.WAITING).stream()
                .map(OrdersDto.OrdersRes::from)
                .toList();
    }

    @Transactional
    public void create(OrdersDto.OrdersReq req) {
        // 0. 주문 아이템 리스트 검증
        if (req.getOrdersItemList() == null || req.getOrdersItemList().isEmpty()) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        // 1. store 조회
        Store store = storeRepository.findById(req.getStoreIdx())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        // 2. Product 조회 및 총 가격 계산
        long totalprice = 0;
        List<OrdersItem> itemList = new ArrayList<>();

        for(OrdersItemDto.OrdersItemReq itemReq : req.getOrdersItemList()) {
            Product product = productRepository.findById(itemReq.getProductIdx())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

            totalprice += (long) product.getUnitPrice() * itemReq.getCount();

            itemList.add(OrdersItem.builder()
                    .count(itemReq.getCount())
                    .product(product)
                    .build());
        }

        // 3. Orders 저장
        Orders orders = ordersRepository.save(Orders.builder()
                .price(totalprice)
                .ordersType(OrdersType.MANUAL)
                .ordersStatus(OrdersStatus.WAITING)
                .isDanger(false)
                .createdAt(LocalDateTime.now())
                .store(store)
                .build());

        // 4. OrdersItem 저장
        for(OrdersItem item : itemList) {
            ordersItemRepository.save(OrdersItem.builder()
                    .count(item.getCount())
                    .product(item.getProduct())
                    .orders(orders)
                    .build());
        }
    }

    public List<OrdersDto.OrdersRes> findAll() {
        return ordersRepository.findAll().stream()
                .map(OrdersDto.OrdersRes::from)
                .toList();
    }

    public OrdersDto.OrdersRes findById(Long ordersIdx) {
        Orders orders = ordersRepository.findById(ordersIdx).orElseThrow(
                () -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));
        return OrdersDto.OrdersRes.from(orders);
    }

    public DangerDto.DangerRes find() {
        return dangerRepository.findById(1L)
                .map(DangerDto.DangerRes::from)
                .orElse(DangerDto.DangerRes.builder()
                        .ratio(200)
                        .period(3)
                        .build());
    }

    public void save(DangerDto.DangerReq req) {
        Danger danger = dangerRepository.findById(1L)
                .orElse(Danger.builder().build());
        danger.update(req.getRatio(), req.getPeriod());
        dangerRepository.save(danger);
    }

    public List<OrdersDto.OrdersRes> findByUserIdx(Long userIdx) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        return ordersRepository.findAllByStore_IdxAndOrdersStatus(store.getIdx(), OrdersStatus.APPROVE).stream()
                .map(OrdersDto.OrdersRes::from)
                .toList();
    }
}
