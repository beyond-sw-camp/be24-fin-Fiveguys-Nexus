package com.example.nexus.domain.settlement;

import com.example.nexus.domain.settlement.model.SettlementDto;
import com.example.nexus.domain.store.StoreService;
import com.example.nexus.domain.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class SettlementController {

    private final SettlementService settlementService;
    private final StoreService storeService;

    @PostMapping("/pay")
    public ResponseEntity<List<SettlementDto.PayRes>> pay (@AuthenticationPrincipal AuthUserDetails authUserDetails
                                                ,@RequestBody SettlementDto.PayReq dto) {

        Long storeIdx = storeService.findStoreIdx(authUserDetails.getIdx());

        dto.setStoreIdx(storeIdx);

        List<SettlementDto.PayRes> settlementList = settlementService.findByStoreIdx(dto);

        return ResponseEntity.ok(settlementList);
    }

}
