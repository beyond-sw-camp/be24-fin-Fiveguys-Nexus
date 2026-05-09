package com.example.nexus.domain.wastelog;

import com.example.nexus.domain.pos.model.PosStoreInventoryDto;
import com.example.nexus.domain.store.StoreInventoryRepository;
import com.example.nexus.domain.store.StoreService;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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


    // 유통기한 지난 재고 조회
    @GetMapping("/over/due-date")
    public ResponseEntity<List<Long>> findOverDueDateProducts (@AuthenticationPrincipal AuthUserDetails authUserDetails) {

        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        List<StoreInventoryDto.ListRes> listRes = storeService.listByStoreIdx(storeIdx);
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


        return ResponseEntity.ok(overDueDateInventoryIdxList);

    }

}
