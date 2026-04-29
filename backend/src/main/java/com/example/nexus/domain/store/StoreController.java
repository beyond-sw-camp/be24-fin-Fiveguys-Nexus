package com.example.nexus.domain.store;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.store.model.StoreDto;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequestMapping("/store")
@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    // [본사] 선택한 가맹점 store_idx로 재고 조회
    @GetMapping("/inventory/list/{storeIdx}")
    public ResponseEntity<List<StoreInventoryDto.ListRes>> listByStoreIdx(@PathVariable Long storeIdx) {

        List<StoreInventoryDto.ListRes> result = storeService.listByStoreIdx(storeIdx);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<?> listStore() {
        List<StoreDto.StoreListRes> result = storeService.list();

        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
