package com.example.nexus.domain.wastelog;

import com.example.nexus.domain.pos.model.PosStoreInventoryDto;
import com.example.nexus.domain.store.StoreInventoryRepository;
import com.example.nexus.domain.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/wastelog")
@RestController
@RequiredArgsConstructor
public class WasteLogController {

    private final StoreService storeService;

    // 유통기한 지난 재고 조회
    @GetMapping("/over/due-date")
    public ResponseEntity<List<PosStoreInventoryDto>> findOverDueDateProducts () {

        // store_inventory 와 pos_store_inventory 테이블에서 manufactured_date와
        // product 테이블에서 danger_days 를 더해서
        // 오늘 날짜와 비교해서 이전이라면
        // 폐기 리스트에 담아주기








    }










}
