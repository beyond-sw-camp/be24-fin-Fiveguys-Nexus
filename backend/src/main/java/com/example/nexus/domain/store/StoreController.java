package com.example.nexus.domain.store;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.store.model.StoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/store")
@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/list")
    public ResponseEntity list(){
        List<StoreDto.StoreListRes> result = storeService.list();

        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
