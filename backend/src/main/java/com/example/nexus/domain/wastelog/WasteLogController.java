package com.example.nexus.domain.wastelog;


import com.example.nexus.domain.store.StoreService;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.user.model.AuthUserDetails;
import com.example.nexus.domain.wastelog.model.WasteLogDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.time.LocalDateTime;
import java.time.YearMonth;
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



    // [가맹점주] POS 재고 lot 기준 폐기
    @PostMapping("/waste/pos")
    public ResponseEntity<BaseResponse<WasteLogDto.WasteRes>> createWasteFromPos(@AuthenticationPrincipal AuthUserDetails userDetails, @RequestBody WasteLogDto.PosWasteReq req) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        WasteLogDto.WasteRes result = wasteLogService.createWaste(userDetails.getIdx(), req);
        return ResponseEntity.ok(BaseResponse.success(result));
    }



    @GetMapping("/compare")
        public ResponseEntity<BaseResponse<String>> compareWasteLog() {
        YearMonth lastMonth = YearMonth.from(LocalDateTime.now()).minusMonths(1);
        YearMonth thisMonth = YearMonth.from(LocalDateTime.now());

        long lastMonthWasteQuantitySum = wasteLogService.getLastMonthWasteSum(lastMonth);
        long thisMonthWasteQuantitySum = wasteLogService.getLastMonthWasteSum(thisMonth);

        return ResponseEntity.ok(BaseResponse.success(lastMonthWasteQuantitySum/thisMonthWasteQuantitySum));
        
    }

}
