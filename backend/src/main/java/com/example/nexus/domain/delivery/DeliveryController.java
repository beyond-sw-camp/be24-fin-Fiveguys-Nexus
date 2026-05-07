package com.example.nexus.domain.delivery;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.domain.delivery.model.DeliveryDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/delivery")
@RestController
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;

    // 본사 배송 관리 페이지 전체 배송현황 리스트 조회
    // 검색 및 필터 포함
    @GetMapping("/head")
    public ResponseEntity<List<DeliveryDto>> getAllDeliveries(
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByHead(storeName, status, year, month, day));
    }

    // 본인 가맹점 배송 현황 조회 (발주 번호 및 날짜/상태 필터 추가)
    @GetMapping("/store")
    public ResponseEntity<List<DeliveryDto>> getMyStoreDeliveries(
            @AuthenticationPrincipal AuthUserDetails authUserDetails,
            @RequestParam(required = false) Long orderIdx,
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day) {

        if (authUserDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Long storeIdx = authUserDetails.getIdx();

        List<DeliveryDto> response = deliveryService.getDeliveriesForStore(
                storeIdx, orderIdx, status, year, month, day
        );

        return ResponseEntity.ok(response);
    }

    // 본사 배송 지연 사유 입력
    @PatchMapping("/head/{deliveryIdx}/delayreason")
    public ResponseEntity<String> updateDelayReason(
            @PathVariable Long deliveryIdx,
            @RequestBody DeliveryDto.DelayReasonRequest request) {

        boolean isSuccess = deliveryService.updateDelayReason(deliveryIdx, request.getDelayReason());

        if (isSuccess) {
            return ResponseEntity.ok("배송 지연 사유가 성공적으로 등록되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("존재하지 않는 배송 정보입니다.");
        }
    }
}
