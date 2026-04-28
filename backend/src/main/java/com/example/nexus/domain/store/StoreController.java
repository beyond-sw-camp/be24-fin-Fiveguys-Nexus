package com.example.nexus.domain.store;

import com.example.nexus.domain.user.model.AuthUserDetails;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequestMapping("/store")
@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    // 가맹점 재고 조회
    @GetMapping("/inventory/list")
    public ResponseEntity<List<StoreInventoryDto.ListRes>> list(@AuthenticationPrincipal AuthUserDetails userDetails){
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }
        List<StoreInventoryDto.ListRes> result = storeService.listByUserIdx(userDetails.getIdx());
        return ResponseEntity.ok(result);
    }
}
