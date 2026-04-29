package com.example.nexus.domain.store;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.store.model.StoreDto;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // [본사] keyword로 가맹점 검색
    @GetMapping("/search")
    public ResponseEntity searchStore(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        StoreDto.StoreSearchReq reqDto = new StoreDto.StoreSearchReq(keyword);

        List<StoreDto.StoreSearchRes> result = storeService.searchByStoreName(reqDto);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/list")
    public ResponseEntity storeList(){
        List<StoreDto.StoreListRes> result = storeService.storeList();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 가맹점 목록 상세 조회
    @GetMapping("/detail/list/{storeIdx}")
    public ResponseEntity storeDetailList(@PathVariable Long storeIdx){
        StoreDto.StoreDetailListRes result = storeService.storeDetailList(storeIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
