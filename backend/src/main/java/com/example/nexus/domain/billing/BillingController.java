package com.example.nexus.domain.billing;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.billing.model.BillingDto;
import com.example.nexus.domain.store.StoreService;
import com.example.nexus.domain.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;
    private final StoreService storeService;

    @PostMapping("/issue")
    public ResponseEntity issueBillingKey(@AuthenticationPrincipal AuthUserDetails authUserDetails, @RequestBody BillingDto.IssueBillingKeyRequestDto request) {
        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());
        if (storeIdx == null) {
            return ResponseEntity.status(403).body(BaseResponse.fail(com.example.nexus.common.model.BaseResponseStatus.PAYMENT_ENROLL_INVALID_USER));
        }
        billingService.issueBillingKey(storeIdx, request);
        return ResponseEntity.ok(BaseResponse.success("빌링키 발급 및 등록 성공"));
    }

    @GetMapping("/list")
    public ResponseEntity getBillingList(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());
        if (storeIdx == null) {
            return ResponseEntity.ok(BaseResponse.success(List.of()));
        }
        return ResponseEntity.ok(BaseResponse.success(billingService.getBillingListByStore(storeIdx)));
    }

    @DeleteMapping("/delete/{storeIdx}")
    public ResponseEntity deleteBillingInfo(@PathVariable Long storeIdx) {
        billingService.deleteBillingInfo(storeIdx);
        return ResponseEntity.ok(BaseResponse.success("빌링 정보 삭제 성공"));
    }
}
