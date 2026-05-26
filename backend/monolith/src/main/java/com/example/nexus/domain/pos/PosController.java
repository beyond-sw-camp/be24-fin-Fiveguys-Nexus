package com.example.nexus.domain.pos;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.common.model.PageResponse;
import com.example.nexus.domain.pos.model.PosCloseDto;
import com.example.nexus.domain.pos.model.PosPayDto;
import com.example.nexus.domain.pos.model.PosStoreInventoryDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequestMapping("/pos")
@RestController
@RequiredArgsConstructor
public class PosController {
    private final PosService posService;
    private final AutoOrderService autoOrderService;

    // [가맹점] 로그인한 가맹점주(STORE)의 user_idx로 해당 매장 재고 조회
    @GetMapping("/inventory/list")
    public ResponseEntity<BaseResponse<PageResponse<PosStoreInventoryDto.ListRes>>> list(@AuthenticationPrincipal AuthUserDetails userDetails, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        PageResponse<PosStoreInventoryDto.ListRes> result = posService.listByUserIdxPaged(userDetails.getIdx(), page, size);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [가맹점] 로그인한 가맹점주(STORE)의 자신의 POS 재고 수량 수정 + 본사가 조회하는 가맹점 재고에도 반영
    @PatchMapping("/inventory/{posStoreInventoryIdx}")
    public ResponseEntity<BaseResponse<PosStoreInventoryDto.SyncCountRes>> changeInventoryCount(@AuthenticationPrincipal AuthUserDetails userDetails, @PathVariable Long posStoreInventoryIdx, @RequestBody PosStoreInventoryDto.CountReq req) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        PosStoreInventoryDto.SyncCountRes result = posService.changeCount(userDetails.getIdx(), posStoreInventoryIdx, req.getCount());

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [가맹점] POS 결제 저장, 결제 금액은 메뉴 단가 기준으로 계산
    @PostMapping("/pay")
    public ResponseEntity<BaseResponse<PosPayDto.PayRes>> pay(@AuthenticationPrincipal AuthUserDetails userDetails, @Valid @RequestBody PosPayDto.PayReq req) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        PosPayDto.PayRes result = posService.pay(userDetails.getIdx(), req);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [가맹점] POS 당일 결제 내역 조회
    @GetMapping("/pay/today")
    public ResponseEntity<BaseResponse<List<PosPayDto.TodayPayRes>>> todayPays(@AuthenticationPrincipal AuthUserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        List<PosPayDto.TodayPayRes> result = posService.listTodayPayHistory(userDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [가맹점] POS 영업 마감: 당일 결제 기준 store_inventory 차감 후 자동 발주
    @PostMapping("/close")
    public ResponseEntity<BaseResponse<PosCloseDto.CloseRes>> close(@AuthenticationPrincipal AuthUserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        PosCloseDto.CloseRes res = posService.deductOnClose(userDetails.getIdx());
        autoOrderService.generateAutoOrder(userDetails.getIdx());
        PosCloseDto.CloseRes body = PosCloseDto.CloseRes.builder()
                .storeIdx(res.getStoreIdx())
                .processedPayCount(res.getProcessedPayCount())
                .deductedProductKinds(res.getDeductedProductKinds())
                .closedAt(res.getClosedAt())
                .message(res.getMessage() + " 자동 발주를 실행했습니다.")
                .build();
        return ResponseEntity.ok(BaseResponse.success(body));
    }
}
