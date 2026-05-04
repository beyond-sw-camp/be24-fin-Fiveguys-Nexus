package com.example.nexus.domain.orders;

import com.example.nexus.common.enums.OrdersStatus;
import com.example.nexus.common.enums.OrdersType;
import com.example.nexus.common.exception.BaseException;
import com.example.nexus.common.model.BaseResponseStatus;
import com.example.nexus.domain.inventory.InventoryMovementService;
import com.example.nexus.domain.inventory.model.InventoryMovementDto;
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
    private final InventoryMovementService inventoryMovementService;

    // 자동 발주 제안 검색 조회 (AUTO + WAITING 상태 대상)
    // 매장명 키워드 검색 + 페이징 처리
    public Page<OrdersDto.OrderListRes> findAllAuto(String keyword, Pageable pageable) {
        Specification<Orders> spec = OrdersSpecification.ordersTypeEquals(OrdersType.AUTO)
                .and(OrdersSpecification.statusIn(List.of(OrdersStatus.WAITING)));

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(OrdersSpecification.keywordLike(keyword));
        }

        return ordersRepository.findAll(spec, pageable).map(OrdersDto.OrderListRes::from);
    }

    // 확정 발주 검색 조회 (CONFIRMED 상태 대상)
    // 매장명 키워드 검색 + 페이징 처리
    public Page<OrdersDto.OrderListRes> findAllConfirmed(String keyword, Pageable pageable) {
        Specification<Orders> spec = OrdersSpecification.statusIn(List.of(OrdersStatus.CONFIRMED));

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(OrdersSpecification.keywordLike(keyword));
        }

        return ordersRepository.findAll(spec, pageable).map(OrdersDto.OrderListRes::from);
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

    // 이상 발주 검색 조회 (isDanger=true 대상)
    // 기간, 키워드 조건으로 필터링 + 페이징 처리
    // 각 발주에 대해 같은 매장의 최근 N개월 평균 수량 계산
    public Page<DangerDto.DangerListRes> findDangerOrders(LocalDate startDate, LocalDate endDate, String keyword, Pageable pageable) {
        Specification<Orders> spec = OrdersSpecification.isDangerTrue();

        if (startDate != null) {
            spec = spec.and(OrdersSpecification.createdAfter(startDate));
        }
        if (endDate != null) {
            spec = spec.and(OrdersSpecification.createdBefore(endDate));
        }
        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(OrdersSpecification.keywordLike(keyword));
        }

        Danger danger = dangerRepository.findById(1L).orElse(null);
        int period = danger != null ? danger.getPeriod() : 3;

        return ordersRepository.findAll(spec, pageable).map(orders -> {
            LocalDateTime since = orders.getCreatedAt().minusMonths(period);
            Integer avgQty = ordersRepository.findAvgQtyByStoreAndPeriod(
                    orders.getStore().getIdx(), since, orders.getIdx());
            return DangerDto.DangerListRes.from(orders, avgQty);
        });
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

    @Transactional
    public void save(DangerDto.DangerReq req) {
        Danger danger = dangerRepository.findById(1L)
                .orElse(Danger.builder().build());

        danger.update(req.getRatio(), req.getPeriod());

        dangerRepository.save(danger);

        // 기존 이상 발주 재평가: 새 기준 미달 시 isDanger 해제
        List<Orders> dangerOrders = ordersRepository.findAllByIsDangerTrue();

        for (Orders orders : dangerOrders) {
            int totalQty = orders.getOrdersItemList().stream().mapToInt(OrdersItem::getCount).sum();

            LocalDateTime since = orders.getCreatedAt().minusMonths(req.getPeriod());

            Integer avgQty = ordersRepository.findAvgQtyByStoreAndPeriod(orders.getStore().getIdx(), since, orders.getIdx());

            int ratio = avgQty > 0 ? (totalQty - avgQty) * 100 / avgQty : 0;

            if (ratio < req.getRatio()) {
                orders.clearDanger();
            }

        }
    }

    @Transactional
    public void approve(Long ordersIdx) {
        Orders orders = ordersRepository.findById(ordersIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        if (orders.getOrdersStatus() != OrdersStatus.CONFIRMED) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        applyOutboundForOrder(orders, "발주 승인(이상) ordersIdx=");
        orders.approve();
    }

    @Transactional
    public void reject(Long ordersIdx) {
        Orders orders = ordersRepository.findById(ordersIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        if (orders.getOrdersStatus() != OrdersStatus.CONFIRMED) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        orders.reject();
    }

    @Transactional
    public void approveAllConfirmed() {
        List<Orders> confirmedOrders = ordersRepository.findAllByOrdersStatus(OrdersStatus.CONFIRMED);

        for (Orders orders : confirmedOrders) {
            applyOutboundForOrder(orders, "발주 일괄승인 ordersIdx=");
            orders.approve();
        }
    }

    private void applyOutboundForOrder(Orders orders, String memoPrefix) {
        Long storeIdx = orders.getStore().getIdx();
        List<OrdersItem> items = ordersItemRepository.findByOrdersIdx(orders.getIdx());
        if (items.isEmpty()) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        String memo = memoPrefix + orders.getIdx();
        for (OrdersItem item : items) {
            InventoryMovementDto.OutboundReq outboundReq = new InventoryMovementDto.OutboundReq(
                    item.getProduct().getIdx(),
                    storeIdx,
                    item.getCount(),
                    memo
            );
            inventoryMovementService.outbound(outboundReq);
        }
    }

    /**
     * 이상 발주 판정
     * 해당 매장의 최근 N개월 발주 건당 평균 수량 대비
     * 주문 수량의 초과 비율이 기준 이상이면 이상 발주로 판정
     * 예: 평균 10개, 이번 30개, 기준 200% → (30-10)*100/10 = 200% → 이상 발주
     */
    private boolean evaluateDanger(Long storeIdx, int totalQty, LocalDateTime baseTime, Long excludeIdx) {
        Danger danger = dangerRepository.findById(1L).orElse(null);
        int dangerRatio = danger != null ? danger.getRatio() : 200;
        int period = danger != null ? danger.getPeriod() : 3;

        LocalDateTime since = baseTime.minusMonths(period);
        Integer avgQty = ordersRepository.findAvgQtyByStoreAndPeriod(storeIdx, since, excludeIdx);
        return avgQty > 0 && (totalQty - avgQty) * 100 / avgQty >= dangerRatio;
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

        // 4. 이상 발주 판정
        int totalQty = itemList.stream().mapToInt(OrdersItem::getCount).sum();
        boolean isDanger = evaluateDanger(store.getIdx(), totalQty, LocalDateTime.now(), null);

        // 5. Orders 저장
        Orders orders = ordersRepository.save(Orders.builder()
                .price(totalprice)
                .ordersType(OrdersType.MANUAL)
                .ordersStatus(OrdersStatus.CONFIRMED)
                .isDanger(isDanger)
                .createdAt(LocalDateTime.now())
                .store(store)
                .build());

        // 6. OrdersItem 저장
        for (OrdersItem item : itemList) {
            ordersItemRepository.save(OrdersItem.builder()
                    .count(item.getCount())
                    .product(item.getProduct())
                    .orders(orders)
                    .build());
        }
    }

    public List<OrdersDto.OrdersRes> findByUserIdxAndOrdersStatus(Long userIdx) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        return ordersRepository.findAllByStore_IdxAndOrdersStatus(store.getIdx(), OrdersStatus.WAITING).stream()
                .map(OrdersDto.OrdersRes::from)
                .toList();
    }

    @Transactional
    public void confirmStoreOrder(Long userIdx, Long ordersIdx) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        Orders orders = ordersRepository.findById(ordersIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        if (!orders.getStore().getIdx().equals(store.getIdx())) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        if (orders.getOrdersStatus() != OrdersStatus.WAITING) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        // 이상 발주 재판정: 점주가 아이템을 수정했을 수 있으므로 확정 시점에 재평가
        int totalQty = orders.getOrdersItemList().stream().mapToInt(OrdersItem::getCount).sum();
        orders.markDanger(evaluateDanger(store.getIdx(), totalQty, orders.getCreatedAt(), orders.getIdx()));

        orders.confirm();
    }

    @Transactional
    public void addStoreItem(Long userIdx, Long ordersIdx, OrdersItemDto.OrdersItemReq req) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        Orders orders = ordersRepository.findById(ordersIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        if (!orders.getStore().getIdx().equals(store.getIdx())) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

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
    public void updateStoreItemCount(Long userIdx, Long ordersItemIdx, Integer count) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        OrdersItem item = ordersItemRepository.findById(ordersItemIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        if (!item.getOrders().getStore().getIdx().equals(store.getIdx())) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        Orders orders = item.getOrders();
        long priceDiff = (long) item.getProduct().getUnitPrice() * (count - item.getCount());
        item.updateCount(count);
        orders.updatePrice(orders.getPrice() + priceDiff);
    }

    @Transactional
    public void deleteStoreItem(Long userIdx, Long ordersItemIdx) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        OrdersItem item = ordersItemRepository.findById(ordersItemIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        if (!item.getOrders().getStore().getIdx().equals(store.getIdx())) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        Orders orders = item.getOrders();
        orders.updatePrice(orders.getPrice() - (long) item.getProduct().getUnitPrice() * item.getCount());
        ordersItemRepository.delete(item);
    }

    @Transactional
    public void rejectStoreOrder(Long userIdx, Long ordersIdx) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        Orders orders = ordersRepository.findById(ordersIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        if (!orders.getStore().getIdx().equals(store.getIdx())) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        if (orders.getOrdersStatus() != OrdersStatus.WAITING) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        orders.reject();
    }

    public List<OrdersDto.OrdersRes> findByUserIdx(Long userIdx) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        return ordersRepository.findAllByStore_IdxAndOrdersStatusIn(
                store.getIdx(),
                List.of(OrdersStatus.CONFIRMED, OrdersStatus.APPROVE, OrdersStatus.REJECT, OrdersStatus.CANCELLED)
        ).stream().map(OrdersDto.OrdersRes::from).toList();
    }

    @Transactional
    public void cancelOrder(Long userIdx, Long ordersIdx) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        Orders orders = ordersRepository.findById(ordersIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        if (!orders.getStore().getIdx().equals(store.getIdx())) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        if (orders.getOrdersStatus() != OrdersStatus.CONFIRMED) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }

        orders.cancel();
    }
}
