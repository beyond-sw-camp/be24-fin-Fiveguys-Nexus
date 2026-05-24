package com.example.nexus.domain.wastelog;


import com.example.nexus.domain.store.StoreInventoryRepository;

import com.example.nexus.common.enums.InventoryStatus;
import com.example.nexus.common.exception.BaseException;
import com.example.nexus.common.model.BaseResponseStatus;
import com.example.nexus.domain.pos.PosStoreInventoryRepository;
import com.example.nexus.domain.pos.model.PosStoreInventory;
import com.example.nexus.domain.store.StoreInventoryRepository;
import com.example.nexus.domain.store.StoreRepository;
import com.example.nexus.domain.store.model.Store;

import com.example.nexus.domain.store.model.StoreInventory;
import com.example.nexus.domain.wastelog.model.WasteLog;
import com.example.nexus.domain.wastelog.model.WasteLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WasteLogService {

    private final WasteLogRepository wasteLogRepository;
    private final StoreRepository storeRepository;
    private final PosStoreInventoryRepository posStoreInventoryRepository;
    private final StoreInventoryRepository storeInventoryRepository;

    public WasteLogDto.StatsRes getWasteStats() {
        YearMonth lastMonth = YearMonth.from(LocalDateTime.now()).minusMonths(1);
        YearMonth thisMonth = YearMonth.from(LocalDateTime.now());

        long lastMonthWasteSum = getInputMonthWasteSum(lastMonth);
        long thisMonthWasteSum = getInputMonthWasteSum(thisMonth);
        
        long lastMonthAmountSum = getInputMonthWasteAmountSum(lastMonth);
        long thisMonthAmountSum = getInputMonthWasteAmountSum(thisMonth);

        Float wasteGradient = lastMonthWasteSum > 0 ? (float) thisMonthWasteSum / lastMonthWasteSum : null;
        long wasteSavingAmount = Math.max(0, lastMonthAmountSum - thisMonthAmountSum);

        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(5).withDayOfMonth(1).toLocalDate().atStartOfDay();
        List<Object[]> monthlyTrendRaw = wasteLogRepository.findMonthlyWasteTrend(sixMonthsAgo);
        List<WasteLogDto.MonthlyTrend> monthlyTrend = monthlyTrendRaw.stream()
                .map(obj -> WasteLogDto.MonthlyTrend.builder()
                        .month((String) obj[0])
                        .quantity(obj[1] != null ? ((Number) obj[1]).longValue() : 0L)
                        .build())
                .collect(Collectors.toList());

        LocalDateTime thisMonthStart = thisMonth.atDay(1).atStartOfDay();
        LocalDateTime thisMonthEnd = thisMonth.atEndOfMonth().atTime(LocalTime.MAX);

        List<Object[]> categoryRatioRaw = wasteLogRepository.findCategoryWasteRatio(thisMonthStart, thisMonthEnd);
        List<WasteLogDto.CategoryRatio> categoryRatio = categoryRatioRaw.stream()
                .map(obj -> WasteLogDto.CategoryRatio.builder()
                        .categoryName((String) obj[0])
                        .quantity(obj[1] != null ? ((Number) obj[1]).longValue() : 0L)
                        .build())
                .collect(Collectors.toList());

        List<Object[]> storeStatusRaw = wasteLogRepository.findStoreWasteStatus(thisMonthStart, thisMonthEnd);
        long totalWarned = 0;
        long totalSuccess = 0;
        List<WasteLogDto.StoreStatus> storeStatus = new ArrayList<>();
        
        for (Object[] obj : storeStatusRaw) {
            long total = obj[1] != null ? ((Number) obj[1]).longValue() : 0L;
            long manual = obj[2] != null ? ((Number) obj[2]).longValue() : 0L;
            long expired = obj[3] != null ? ((Number) obj[3]).longValue() : 0L;
            
            // Assuming "warned" corresponds to total items that reached critical status? 
            // For now, let's use the query results mapping to DTO
            double rate = total > 0 ? (double) manual / total * 100 : 0.0;
            
            totalWarned += total;
            totalSuccess += manual;
            
            storeStatus.add(WasteLogDto.StoreStatus.builder()
                    .storeName((String) obj[0])
                    .warnedCount(total)
                    .successCount(manual)
                    .failCount(expired)
                    .successRate(rate)
                    .build());
        }

        float overallSuccessRate = totalWarned > 0 ? (float) totalSuccess / totalWarned * 100 : 0.0f;

        return WasteLogDto.StatsRes.builder()
                .wasteSum(thisMonthWasteSum)
                .lastMonthWasteSum(lastMonthWasteSum)
                .wasteGradient(wasteGradient)
                .useSuccessRate(overallSuccessRate)
                .wasteSavingAmount(wasteSavingAmount)
                .avgTurnover(0.0f)
                .optimalStockRate(0.0f)
                .overstockCount(0)
                .autoOrderAccuracy(0.0f)
                .monthlyWasteTrend(monthlyTrend)
                .categoryWasteRatio(categoryRatio)
                .storeWasteStatus(storeStatus)
                .storeTurnoverTrend(new ArrayList<>())
                .storeOverstockTrend(new ArrayList<>())
                .build();
    }

    public List<WasteLogDto.RegRes> registerOverDueDateInventories(List<Long> idxList) {

        List<WasteLogDto.RegRes> resList = new ArrayList<>();

        for(Long idx : idxList) {
            StoreInventory inventory = storeInventoryRepository.findById(idx).orElse(null);
            WasteLog entity = WasteLog.builder()
                    .quantity(inventory.getCount())
                    .amountLoss(inventory.getProduct().getUnitPrice())
                    .wasteDate(inventory.getManufacturedDate().plusDays(Long.parseLong(inventory.getProduct().getDangerDays())))
                    .wasteReason("유통기한 만료")
                    .store(inventory.getStore())
                    .product(inventory.getProduct())
                    .build();
            wasteLogRepository.save(entity);

            resList.add(WasteLogDto.RegRes.from(entity));
        }

        return resList;
    }



    @Transactional
    public WasteLogDto.WasteRes createWaste(Long userIdx, WasteLogDto.PosWasteReq req) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.STORE_NOT_FOUND));

        PosStoreInventory posLot = posStoreInventoryRepository.findById(req.getPosStoreInventoryIdx())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.POS_INVENTORY_NOT_FOUND));

        if (!posLot.getStore().getIdx().equals(store.getIdx())) {
            throw new BaseException(BaseResponseStatus.STORE_INVENTORY_NOT_AUTHORIZED);
        }

        if (posLot.getCount() < req.getQuantity()) {
            throw new BaseException(BaseResponseStatus.WASTE_QUANTITY_EXCEEDS_STOCK);
        }

        StoreInventory hqLot = storeInventoryRepository
                .findByStore_IdxAndProduct_IdxAndManufacturedDate(
                        store.getIdx(), posLot.getProduct().getIdx(), posLot.getManufacturedDate())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.STORE_INVENTORY_NOT_FOUND));

        posLot.setCount(posLot.getCount() - req.getQuantity());
        hqLot.setCount(hqLot.getCount() - req.getQuantity());

        posLot.setStatus(resolveStatus(posLot.getCount(), posLot.getProduct().getMinStock()));
        hqLot.setStatus(resolveStatus(hqLot.getCount(), hqLot.getProduct().getMinStock()));

        long amountLoss = (long) req.getQuantity() * posLot.getProduct().getUnitPrice();

        WasteLog wasteLog = new WasteLog();
        wasteLog.setQuantity(req.getQuantity());
        wasteLog.setAmountLoss((int) amountLoss);
        wasteLog.setWasteDate(LocalDateTime.now());
        wasteLog.setWasteReason(req.getWasteReason());
        wasteLog.setStore(store);
        wasteLog.setProduct(posLot.getProduct());

        return WasteLogDto.WasteRes.from(wasteLogRepository.save(wasteLog));
    }

    private InventoryStatus resolveStatus(int count, int minStock) {
        if (count <= 0) return InventoryStatus.CRITICAL;
        // TODO: 유통기한 임박 상태 변경 로직 수정 필요
        if (count <= minStock / 2) return InventoryStatus.CRITICAL;
        if (count <= minStock) return InventoryStatus.LOW;

        return InventoryStatus.NORMAL;
    }

    public Long getInputMonthWasteSum(YearMonth inputYearMonth) {

        LocalDateTime startDateTime = inputYearMonth.atDay(1).atStartOfDay();

        LocalDateTime endDateTime = inputYearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        List<WasteLog> logs = wasteLogRepository.findAllByWasteDateBetween(startDateTime, endDateTime);
        Long wasteSum = 0L;
        for (WasteLog log : logs) {
            wasteSum += log.getQuantity();
        }
        return wasteSum;
    }

    public Long getInputMonthWasteAmountSum(YearMonth inputYearMonth) {
        LocalDateTime startDateTime = inputYearMonth.atDay(1).atStartOfDay();
        LocalDateTime endDateTime = inputYearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        List<WasteLog> logs = wasteLogRepository.findAllByWasteDateBetween(startDateTime, endDateTime);
        Long amountSum = 0L;
        for (WasteLog log : logs) {
            amountSum += log.getAmountLoss();
        }
        return amountSum;
    }

}
