package com.example.nexus.domain.pos;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.common.model.BaseResponseStatus;
import com.example.nexus.domain.menu.MenuItemRepository;
import com.example.nexus.domain.menu.MenuRepository;
import com.example.nexus.domain.menu.model.Menu;
import com.example.nexus.domain.menu.model.MenuItem;
import com.example.nexus.domain.pos.model.PosOrdersItem;
import com.example.nexus.domain.pos.model.PosPay;
import com.example.nexus.domain.pos.model.PosPayDto;
import com.example.nexus.domain.pos.model.PosStoreInventory;
import com.example.nexus.domain.pos.model.PosStoreInventoryDto;
import com.example.nexus.domain.store.StoreInventoryRepository;
import com.example.nexus.domain.store.StoreRepository;
import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreInventory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PosService {

    private record MenuOrderLine(Menu menu, int quantity) {}
    private final PosStoreInventoryRepository posStoreInventoryRepository;
    private final StoreRepository storeRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final MenuRepository menuRepository;
    private final MenuItemRepository menuItemRepository;
    private final PosPayRepository posPayRepository;
    private final PosOrdersItemRepository posOrdersItemRepository;

    public List<PosStoreInventoryDto.ListRes> listByUserIdx(Long idx) {
        Store store = storeRepository.findByUserIdx(idx).orElseThrow();

        Long storeIdx = store.getIdx();

        List<PosStoreInventory> inventoryList = posStoreInventoryRepository.findByStoreIdx(storeIdx);

        return inventoryList.stream().map(PosStoreInventoryDto.ListRes::from).toList();
    }

    public PosStoreInventoryDto.SyncCountRes changeCount(Long userIdx, Long posStoreInventoryIdx, Integer count) {
        Store myStore = storeRepository.findByUserIdx(userIdx).orElseThrow();

        PosStoreInventory posInventory = posStoreInventoryRepository.findById(posStoreInventoryIdx).orElseThrow();
        StoreInventory hqInventory = storeInventoryRepository.findById(posStoreInventoryIdx).orElseThrow();

        if (!posInventory.getStore().getIdx().equals(myStore.getIdx())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 매장 POS 재고만 수정할 수 있습니다.");
        }

        posInventory.setCount(count);
        hqInventory.setCount(count);

        PosStoreInventory posSaved = posStoreInventoryRepository.save(posInventory);
        StoreInventory hqSaved = storeInventoryRepository.save(hqInventory);

        return PosStoreInventoryDto.SyncCountRes.from(posSaved, hqSaved);
    }

    /**
     * POS 결제 시 {@code pos_store_inventory}만 차감한다. 본사/가맹점 공식 재고({@code store_inventory})는 마감 처리 시에만 반영한다.
     */
    @Transactional
    public PosPayDto.PayRes pay(Long userIdx, PosPayDto.PayReq req) {
        Store store = storeRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));

        List<MenuOrderLine> lines = new ArrayList<>();

        for (PosPayDto.PayLineReq line : req.getItems()) {
            Menu menu = menuRepository.findById(line.getMenuIdx())
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_DATA));
            if (menu.isDeleted()) {
                throw new BaseException(BaseResponseStatus.NOT_FOUND_DATA);
            }
            lines.add(new MenuOrderLine(menu, line.getQuantity()));
        }

        Map<Long, Integer> productNeedByProductIdx = aggregateProductNeedFromRecipes(lines);

        for (Map.Entry<Long, Integer> e : productNeedByProductIdx.entrySet()) {
            deductProductFromLotsFifo(store, e.getKey(), e.getValue());
        }

        List<PosPayDto.PayLineRes> lineResList = new ArrayList<>();

        long payAmount = 0L;

        for (MenuOrderLine row : lines) {
            long lineAmount = (long) row.menu().getPrice() * row.quantity();
            payAmount += lineAmount;
            lineResList.add(PosPayDto.PayLineRes.builder()
                    .menuIdx(row.menu().getIdx())
                    .menuName(row.menu().getMenuName())
                    .quantity(row.quantity())
                    .unitPrice(row.menu().getPrice())
                    .lineAmount(lineAmount)
                    .build());
        }

        PosPay posPay = PosPay.builder()
                .method(req.getMethod())
                .paidAt(LocalDateTime.now())
                .payAmount(payAmount)
                .store(store)
                .build();

        PosPay savedPay = posPayRepository.save(posPay);

        List<PosOrdersItem> orderLines = new ArrayList<>();

        for (MenuOrderLine row : lines) {
            orderLines.add(PosOrdersItem.builder()
                    .menu(row.menu())
                    .posPay(savedPay)
                    .quantity(row.quantity())
                    .build());
        }
        posOrdersItemRepository.saveAll(orderLines);

        return PosPayDto.PayRes.builder()
                .posPayIdx(savedPay.getIdx())
                .storeIdx(store.getIdx())
                .method(savedPay.getMethod())
                .payAmount(savedPay.getPayAmount())
                .paidAt(savedPay.getPaidAt())
                .items(lineResList)
                .build();
    }

    private Map<Long, Integer> aggregateProductNeedFromRecipes(List<MenuOrderLine> lines) {
        Map<Long, Integer> productNeed = new HashMap<>();
        for (MenuOrderLine row : lines) {
            List<MenuItem> recipe = menuItemRepository.findByMenu_Idx(row.menu().getIdx());

            for (MenuItem mi : recipe) {
                Long productIdx = mi.getProduct().getIdx();
                int need = mi.getQuantity() * row.quantity();
                productNeed.merge(productIdx, need, Integer::sum);
            }
        }
        return productNeed;
    }

    private void deductProductFromLotsFifo(Store store, Long productIdx, int amount) {
        if (amount <= 0) {
            return;
        }
        List<PosStoreInventory> lots = posStoreInventoryRepository
                .findByStore_IdxAndProduct_IdxOrderByManufacturedDateAsc(store.getIdx(), productIdx);
        int remaining = amount;
        for (PosStoreInventory lot : lots) {
            if (remaining <= 0) {
                break;
            }
            int onHand = lot.getCount();
            if (onHand <= 0) {
                continue;
            }
            int take = Math.min(onHand, remaining);
            lot.setCount(onHand - take);
            remaining -= take;
        }
    }
}
