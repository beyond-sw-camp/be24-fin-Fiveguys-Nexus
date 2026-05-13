package com.example.nexus.domain.delivery;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.delivery.model.DeliveryDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/delivery")
@RestController
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // 본사 배송 관리 페이지 전체 배송현황 리스트 조회
    // 검색 및 필터 포함
    @GetMapping("/head")
    public ResponseEntity<BaseResponse<DeliveryDto.DeliveryPageRes>> getAllDeliveries(
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(BaseResponse.success(
                deliveryService.getDeliveriesByHead(
                        storeName,
                        status,
                        year,
                        month,
                        day,
                        page,
                        size
                )
        ));
    }

    // 본인 가맹점 배송 현황 조회
    @GetMapping("/store")
    public ResponseEntity<BaseResponse<DeliveryDto.DeliveryPageRes>> getMyStoreDeliveries(
            @AuthenticationPrincipal AuthUserDetails authUserDetails,
            @RequestParam(required = false) Long orderIdx,
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (authUserDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Long userIdx = authUserDetails.getIdx();

        return ResponseEntity.ok(BaseResponse.success(
                deliveryService.getDeliveriesForStore(
                        userIdx,
                        orderIdx,
                        status,
                        year,
                        month,
                        day,
                        page,
                        size
                )
        ));
    }

    // 본사 배송 지연 사유 입력
    @PatchMapping("/head/delayreason")
    public ResponseEntity<BaseResponse<String>> updateDelayReason(
            @AuthenticationPrincipal AuthUserDetails authUserDetails,
            @Valid @RequestBody DeliveryDto.DelayReasonRequest request) {

        if (authUserDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        boolean isSuccess = deliveryService.updateDelayReason(
                request.getDeliveryIdx(),
                request.getDelayReason()
        );

        if (isSuccess) {
            return ResponseEntity.ok(
                    BaseResponse.success("배송 지연 사유가 성공적으로 등록되었습니다.")
            );
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.success("존재하지 않는 배송 정보이거나 권한이 없습니다."));
    }
}