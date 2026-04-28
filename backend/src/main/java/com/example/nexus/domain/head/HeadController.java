package com.example.nexus.domain.head;

import com.example.nexus.common.model.BaseEntity;
import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.head.model.HeadInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/head")
@RestController
@RequiredArgsConstructor
public class HeadController {
    private final HeadService headIncomeService;
    private final HeadService headService;

    // 본사 재고 조회
    @GetMapping("/inventory/list")
    public ResponseEntity<List<HeadInventoryDto.ListRes>> list(){
        List<HeadInventoryDto.ListRes> result = headService.list();
        return ResponseEntity.ok(result);
    }
}
