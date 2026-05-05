package com.example.nexus.domain.delivery;

import com.example.nexus.common.enums.DeliveryStatus;
import com.example.nexus.domain.delivery.model.DeliveryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 본인 가맹점 배송 현황 조회
    @GetMapping("/store/{storeIdx}")
    public ResponseEntity<List<DeliveryDto>> getStoreDeliveries(@PathVariable Long storeIdx) {
        List<DeliveryDto> response = deliveryService.getDeliveriesForStore(storeIdx);
        return ResponseEntity.ok(response);
    }
}
