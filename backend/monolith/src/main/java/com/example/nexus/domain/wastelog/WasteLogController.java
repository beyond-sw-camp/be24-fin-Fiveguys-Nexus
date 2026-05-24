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
    public ResponseEntity<BaseResponse<WasteLogDto.StatsRes>> compareWasteLog() {
        WasteLogDto.StatsRes resDto = wasteLogService.getWasteStats();
        return ResponseEntity.ok(BaseResponse.success(resDto));
    }

}
