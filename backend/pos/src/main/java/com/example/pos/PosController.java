package com.example.pos;

import com.example.pos.common.model.BaseResponse;
import com.example.pos.common.model.PageResponse;
import com.example.pos.model.PosCloseDto;
import com.example.pos.model.PosPayDto;
import com.example.pos.model.PosStoreInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequestMapping("/pos")
@RestController
@RequiredArgsConstructor
public class PosController {
    private final PosService posService;

    // [가맹점] 로그인한 가맹점주(STORE)의 user_idx로 해당 매장 재고 조회
    @GetMapping("/inventory/list")
    public ResponseEntity<BaseResponse<PageResponse<PosStoreInventoryDto.ListRes>>> list(
            @RequestHeader("X-User-Idx") Long userIdx,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (userIdx == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        PageResponse<PosStoreInventoryDto.ListRes> result = posService.listByUserIdxPaged(userIdx, page, size);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [가맹점] 로그인한 가맹점주(STORE)의 자신의 POS 재고 수량 수정 + 본사가 조회하는 가맹점 재고에도 반영
    @PatchMapping("/inventory/{posStoreInventoryIdx}")
    public ResponseEntity<BaseResponse<PosStoreInventoryDto.SyncCountRes>> changeInventoryCount(
            @RequestHeader("X-User-Idx") Long userIdx,
            @PathVariable Long posStoreInventoryIdx,
            @RequestBody PosStoreInventoryDto.CountReq req) {
        if (userIdx == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        PosStoreInventoryDto.SyncCountRes result = posService.changeCount(userIdx, posStoreInventoryIdx, req.getCount());

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [가맹점] POS 결제 저장, 결제 금액은 메뉴 단가 기준으로 계산
    @PostMapping("/pay")
    public ResponseEntity<BaseResponse<PosPayDto.PayRes>> pay(
            @RequestHeader("X-User-Idx") Long userIdx,
            @Valid @RequestBody PosPayDto.PayReq req) {
        if (userIdx == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        PosPayDto.PayRes result = posService.pay(userIdx, req);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [가맹점] POS 당일 결제 내역 조회
    @GetMapping("/pay/today")
    public ResponseEntity<BaseResponse<List<PosPayDto.TodayPayRes>>> todayPays(
            @RequestHeader("X-User-Idx") Long userIdx) {
        if (userIdx == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        List<PosPayDto.TodayPayRes> result = posService.listTodayPayHistory(userIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [가맹점] POS 영업 마감: 당일 결제 기준 store_inventory 차감 후 자동 발주
    @PostMapping("/close")
    public ResponseEntity<BaseResponse<PosCloseDto.CloseRes>> close(
            @RequestHeader("X-User-Idx") Long userIdx) {
        if (userIdx == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        PosCloseDto.CloseRes res = posService.deductOnClose(userIdx);
        return ResponseEntity.ok(BaseResponse.success(res));
    }
}
