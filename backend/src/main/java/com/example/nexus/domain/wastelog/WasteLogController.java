package com.example.nexus.domain.wastelog;

import com.example.nexus.domain.store.StoreService;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/wastelog")
@RestController
@RequiredArgsConstructor
public class WasteLogController {

    private final StoreService storeService;
    private final WasteLogService wasteLogService;


    @PostMapping("/over/due-date")
    public ResponseEntity findOverDueDateProducts () {

        List<StoreInventoryDto.ListRes> listRes = storeService.findAllStoreInventory();
        // store_inventory 와 pos_store_inventory 테이블에서 manufactured_date와
        // product 테이블에서 danger_days 를 더해서
        // 오늘 날짜와 비교해서 이전이라면
        // 폐기 리스트에 담아주기

        List<Long> overDueDateInventoryIdxList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (StoreInventoryDto.ListRes dto : listRes) {
            if (dto.getExpiryDate().isBefore(now)) {
                overDueDateInventoryIdxList.add(dto.getIdx());
            }
        }

        wasteLogService.registerOverDueDateInventories(overDueDateInventoryIdxList);

        return ResponseEntity.ok("성공");
    }

}
