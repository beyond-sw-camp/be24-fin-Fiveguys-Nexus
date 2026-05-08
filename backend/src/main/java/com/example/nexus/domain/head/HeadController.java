package com.example.nexus.domain.head;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.common.model.PageResponse;
import com.example.nexus.domain.head.model.HeadIncomeDto;
import com.example.nexus.domain.head.model.HeadInventoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/head")
@RestController
@RequiredArgsConstructor
public class HeadController {
    private final HeadService headService;
    private final HeadIncomeService headIncomeService;

    // 본사 재고 조회
    @GetMapping("/inventory/list")
    public ResponseEntity<BaseResponse<PageResponse<HeadInventoryDto.ListRes>>> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        PageResponse<HeadInventoryDto.ListRes> result = headService.listPaged(page, size);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 본사 정산 내역 조회
    @GetMapping("/income")
    public ResponseEntity<BaseResponse<List<HeadIncomeDto.ListRes>>> getIncomeList(
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<HeadIncomeDto.ListRes> result = headIncomeService.getIncomeList(storeName, status, startDate, endDate);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
