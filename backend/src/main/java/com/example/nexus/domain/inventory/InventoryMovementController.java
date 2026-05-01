package com.example.nexus.domain.inventory;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.inventory.model.InventoryMovementDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inventory")
public class InventoryMovementController {
    private final InventoryMovementService inventoryMovementService;

    @PostMapping("/inbound")
    public ResponseEntity inbound(@RequestBody InventoryMovementDto.InboundReq req) {

        InventoryMovementDto.MovementRes result = inventoryMovementService.inbound(req);

        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
