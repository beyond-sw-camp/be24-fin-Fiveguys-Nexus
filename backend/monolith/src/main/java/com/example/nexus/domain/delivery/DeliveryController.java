package com.example.nexus.domain.delivery;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.delivery.model.DeliveryDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "배송 관리", description = "본사 및 가맹점의 배송 현황 조회 및 지연 사유 관리 API")
@RequestMapping("/delivery")
@RestController
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // 본사 배송 관리 페이지 전체 배송현황 리스트 조회
    // 검색 및 필터 포함
    @Operation(summary = "본사 전체 배송 현황 조회", description = "본사 관리자가 모든 가맹점의 배송 현황을 조회합니다. 가맹점명, 배송 상태, 날짜 필터를 지원합니다.")
    @GetMapping("/head")
    public ResponseEntity<BaseResponse<DeliveryDto.DeliveryPageRes>> getAllDeliveries(
            @Parameter(description = "가맹점명 검색어") @RequestParam(required = false) String storeName,
            @Parameter(description = "배송 상태 (READY, START, DELIVERYING, DELIVERED, DELAY)") @RequestParam(required = false) DeliveryStatus status,
            @Parameter(description = "조회 연도") @RequestParam(required = false) Integer year,
            @Parameter(description = "조회 월") @RequestParam(required = false) Integer month,
            @Parameter(description = "조회 일") @RequestParam(required = false) Integer day,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

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
    @Operation(summary = "가맹점 본인 배송 현황 조회", description = "로그인한 가맹점주가 자신의 가맹점에 대한 배송 현황을 조회합니다.")
    @GetMapping("/store")
    public ResponseEntity<BaseResponse<DeliveryDto.DeliveryPageRes>> getMyStoreDeliveries(
            @AuthenticationPrincipal AuthUserDetails authUserDetails,
            @Parameter(description = "발주 번호") @RequestParam(required = false) Long orderIdx,
            @Parameter(description = "배송 상태 (READY, START, DELIVERYING, DELIVERED, DELAY)") @RequestParam(required = false) DeliveryStatus status,
            @Parameter(description = "조회 연도") @RequestParam(required = false) Integer year,
            @Parameter(description = "조회 월") @RequestParam(required = false) Integer month,
            @Parameter(description = "조회 일") @RequestParam(required = false) Integer day,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {

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
    @Operation(summary = "배송 지연 사유 등록", description = "본사 관리자가 배송 지연된 건에 대해 사유를 입력합니다.")
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