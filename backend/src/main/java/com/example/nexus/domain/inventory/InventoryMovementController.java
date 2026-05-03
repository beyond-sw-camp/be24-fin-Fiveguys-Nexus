package com.example.nexus.domain.inventory;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.inventory.model.InventoryMovementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryMovementController {
    private final InventoryMovementService inventoryMovementService;

    // [본사] 제품 입고 (시나리오)
    @PostMapping("/inbound")
    public ResponseEntity<?> inbound(@RequestBody InventoryMovementDto.InboundReq req) {

        InventoryMovementDto.MovementRes result = inventoryMovementService.inbound(req);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [본사] 제품 출고
    @PostMapping("/outbound")
    public ResponseEntity<?> outbound(@RequestBody InventoryMovementDto.OutboundReq req) {

        InventoryMovementDto.MovementRes result = inventoryMovementService.outbound(req);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [본사] 입출고 이력 전체 조회
    @GetMapping("/movements")
    public ResponseEntity<?> listMovements() {
        List<InventoryMovementDto.MovementListRes> result = inventoryMovementService.findAllMovements();
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
