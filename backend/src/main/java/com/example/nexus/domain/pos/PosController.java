package com.example.nexus.domain.pos;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.common.model.BaseResponseStatus;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/pos")
@RestController
@RequiredArgsConstructor
public class PosController {
    private final PosService posService;

    // [가맹점] 로그인한 가맹점주(STORE)의 user_idx로 해당 매장 재고 조회
    @GetMapping("/inventory/list")
    public ResponseEntity<List<PosStoreInventoryDto.ListRes>> list(@AuthenticationPrincipal AuthUserDetails userDetails) {

        List<PosStoreInventoryDto.ListRes> result = posService.listByUserIdx(userDetails.getIdx());

        return ResponseEntity.ok(result);
    }

    // [가맹점] 로그인한 가맹점주(STORE)의 자신의 POS 재고 수량 수정 + 본사가 조회하는 가맹점 재고에도 반영
    @PatchMapping("/inventory/{posStoreInventoryIdx}")
    public ResponseEntity<PosStoreInventoryDto.SyncCountRes> changeInventoryCount(@AuthenticationPrincipal AuthUserDetails userDetails, @PathVariable Long posStoreInventoryIdx, @RequestBody PosStoreInventoryDto.CountReq req) {

        PosStoreInventoryDto.SyncCountRes result = posService.changeCount(userDetails.getIdx(), posStoreInventoryIdx, req.getCount());

        return ResponseEntity.ok(result);
    }

    // [가맹점] POS 결제 저장, 결제 금액은 메뉴 단가 기준으로 계산
    @PostMapping("/pay")
    public ResponseEntity<BaseResponse<PosPayDto.PayRes>> pay(@AuthenticationPrincipal AuthUserDetails userDetails, @Valid @RequestBody PosPayDto.PayReq req) {

        PosPayDto.PayRes result = posService.pay(userDetails.getIdx(), req);

        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
