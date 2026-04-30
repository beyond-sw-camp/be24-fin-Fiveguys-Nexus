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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public List<OrdersDto.OrderListRes> findAllAuto() {
        return ordersRepository.findAllByOrdersTypeAndOrdersStatus(OrdersType.AUTO, OrdersStatus.WAITING).stream()
                .map(OrdersDto.OrderListRes::from)
                .toList();
    }

    @Transactional
    public void createStoreManualOrder(Long userIdx, OrdersDto.OrdersReq req) {
        // 1. 주문 아이템 리스트 검증
        if (req.getOrdersItemList() == null || req.getOrdersItemList().isEmpty()) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        // 2. 인증 정보로 매장 조회
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        // 3. Product 조회 및 총 가격 계산
        long totalprice = 0;
        List<OrdersItem> itemList = new ArrayList<>();

        for (OrdersItemDto.OrdersItemReq itemReq : req.getOrdersItemList()) {
            Product product = productRepository.findById(itemReq.getProductIdx())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

            totalprice += (long) product.getUnitPrice() * itemReq.getCount();

            itemList.add(OrdersItem.builder()
                    .count(itemReq.getCount())
                    .product(product)
                    .build());
        }

        // 4. Orders 저장
        Orders orders = ordersRepository.save(Orders.builder()
                .price(totalprice)
                .ordersType(OrdersType.MANUAL)
                .ordersStatus(OrdersStatus.CONFIRMED)
                .isDanger(false)
                .createdAt(LocalDateTime.now())
                .store(store)
                .build());

        // 5. OrdersItem 저장
        for (OrdersItem item : itemList) {
            ordersItemRepository.save(OrdersItem.builder()
                    .count(item.getCount())
                    .product(item.getProduct())
                    .orders(orders)
                    .build());
        }
    }

    @Transactional
    public void cancelOrder(Long ordersIdx) {
        // 1. 발주 조회
        Orders orders = ordersRepository.findById(ordersIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        // 2. CONFIRMED 상태인 경우에만 취소 가능
        if (orders.getOrdersStatus() != OrdersStatus.CONFIRMED) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        // 3. 상태를 CANCELLED로 변경
        orders.cancel();
    }

    @Transactional
    public void approveAllConfirmed() {
        // 1. CONFIRMED 상태의 모든 발주 조회
        List<Orders> confirmedOrders = ordersRepository.findAllByOrdersStatus(OrdersStatus.CONFIRMED);

        // 2. 각 발주를 APPROVE 상태로 변경
        for (Orders orders : confirmedOrders) {
            orders.approve();
        }
    }

    // 발주 이력 검색 조회 (APPROVE, REJECT, CANCELLED 상태 대상)
    // 발주 유형, 기간, 키워드 조건으로 필터링 + 페이징 처리
    public Page<OrdersDto.OrderListRes> findOrderHistory(OrdersType ordersType, LocalDate startDate, LocalDate endDate, String keyword, Pageable pageable) {
        Specification<Orders> spec = OrdersSpecification.statusIn(
                List.of(OrdersStatus.APPROVE, OrdersStatus.REJECT, OrdersStatus.CANCELLED));

        if (ordersType != null) {
            spec = spec.and(OrdersSpecification.ordersTypeEquals(ordersType));
        }
        if (startDate != null) {
            spec = spec.and(OrdersSpecification.createdAfter(startDate));
        }
        if (endDate != null) {
            spec = spec.and(OrdersSpecification.createdBefore(endDate));
        }
        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(OrdersSpecification.keywordLike(keyword));
        }

        return ordersRepository.findAll(spec, pageable).map(OrdersDto.OrderListRes::from);
    }

    public List<OrdersDto.OrderListRes> findAllConfirmed() {
        return ordersRepository.findAllByOrdersStatus(OrdersStatus.CONFIRMED).stream()
                .map(OrdersDto.OrderListRes::from)
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

    @Transactional
    public void approve(Long ordersIdx) {
        Orders orders = ordersRepository.findById(ordersIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));
        if (orders.isDanger()) {
            orders.clearDanger();
        }
        orders.approve();
    }

    @Transactional
    public void reject(Long ordersIdx) {
        Orders orders = ordersRepository.findById(ordersIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));
        if (orders.isDanger()) {
            orders.clearDanger();
        }
        orders.reject();
    }

    @Transactional
    public void addItem(Long ordersIdx, OrdersItemDto.OrdersItemReq req) {
        Orders orders = ordersRepository.findById(ordersIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        Product product = productRepository.findById(req.getProductIdx())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        ordersItemRepository.save(OrdersItem.builder()
                .count(req.getCount())
                .product(product)
                .orders(orders)
                .build());

        orders.updatePrice(orders.getPrice() + (long) product.getUnitPrice() * req.getCount());
    }

    @Transactional
    public void updateItemCount(Long ordersItemIdx, Integer count) {
        OrdersItem item = ordersItemRepository.findById(ordersItemIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        Orders orders = item.getOrders();
        long priceDiff = (long) item.getProduct().getUnitPrice() * (count - item.getCount());
        item.updateCount(count);
        orders.updatePrice(orders.getPrice() + priceDiff);
    }

    @Transactional
    public void deleteItem(Long ordersItemIdx) {
        OrdersItem item = ordersItemRepository.findById(ordersItemIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        Orders orders = item.getOrders();
        orders.updatePrice(orders.getPrice() - (long) item.getProduct().getUnitPrice() * item.getCount());
        ordersItemRepository.delete(item);
    }

    public List<OrdersDto.OrdersRes> findByUserIdx(Long userIdx) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        return ordersRepository.findAllByStore_IdxAndOrdersStatus(store.getIdx(), OrdersStatus.APPROVE).stream()
                .map(OrdersDto.OrdersRes::from)
                .toList();
    }

    public List<OrdersDto.OrdersRes> findByUserIdxAndOrdersStatus(Long userIdx) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        return ordersRepository.findAllByStore_IdxAndOrdersStatus(store.getIdx(), OrdersStatus.WAITING).stream()
                .map(OrdersDto.OrdersRes::from)
                .toList();
    }
}
