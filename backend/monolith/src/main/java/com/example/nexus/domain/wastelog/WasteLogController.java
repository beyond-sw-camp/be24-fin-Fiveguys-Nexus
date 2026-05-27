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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.user.model.AuthUserDetails;
import com.example.nexus.domain.wastelog.model.WasteLogDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "폐기 관리", description = "유통기한 경과 상품 및 POS 수동 폐기 내역 관리 API")
@RequestMapping("/wastelog")
@RestController
@RequiredArgsConstructor
public class WasteLogController {

    private final StoreService storeService;
    private final WasteLogService wasteLogService;

    @Operation(summary = "유통기한 경과 상품 폐기 등록", description = "모든 가맹점의 재고 중 유통기한이 지난 상품을 찾아 폐기 로그에 등록합니다.")
    @PostMapping("/over/due-date")
    public ResponseEntity<String> findOverDueDateProducts() {

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

    @Operation(summary = "POS 수동 폐기 등록", description = "가맹점주가 POS를 통해 특정 상품(Lot 단위)을 직접 폐기 처리합니다.")
    @PostMapping("/waste/pos")
    public ResponseEntity<BaseResponse<WasteLogDto.WasteRes>> createWasteFromPos(
            @AuthenticationPrincipal AuthUserDetails userDetails,
            @RequestBody WasteLogDto.PosWasteReq req) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        WasteLogDto.WasteRes result = wasteLogService.createWaste(userDetails.getIdx(), req);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "폐기 통계 비교", description = "폐기 내역에 대한 통계 및 분석 데이터를 조회합니다.")
    @GetMapping("/compare")
    public ResponseEntity<BaseResponse<WasteLogDto.StatsRes>> compareWasteLog() {
        WasteLogDto.StatsRes resDto = wasteLogService.getWasteStats();
        return ResponseEntity.ok(BaseResponse.success(resDto));
    }
}
