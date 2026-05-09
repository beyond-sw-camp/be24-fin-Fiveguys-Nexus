package com.example.nexus.domain.store;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.common.model.PageResponse;
import com.example.nexus.domain.store.model.StoreDto;
import com.example.nexus.domain.store.model.StoreIncomeDto;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequestMapping("/store")
@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final StoreIncomeService storeIncomeService;


    // [본사] 선택한 가맹점 store_idx로 재고 조회
    @GetMapping("/inventory/list/{storeIdx}")
    public ResponseEntity<BaseResponse<PageResponse<StoreInventoryDto.ListRes>>> listByStoreIdx(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long storeIdx, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        PageResponse<StoreInventoryDto.ListRes> result = storeService.listByStoreIdxPaged(storeIdx, page, size);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [본사] keyword로 가맹점 검색
    @GetMapping("/search")
    public ResponseEntity searchStore(@AuthenticationPrincipal UserDetails userDetails, @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        StoreDto.StoreSearchReq reqDto = new StoreDto.StoreSearchReq(keyword);

        List<StoreDto.StoreSearchRes> result = storeService.searchByStoreName(reqDto);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 가맹점 목록 조회 및 검색 조건
    @GetMapping("/list")
    public ResponseEntity storeList(
            StoreDto.StoreSearchPagingReq searchReq,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        StoreDto.StorePageRes result = storeService.storeList(searchReq, page,size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/totalCount/list")
    public ResponseEntity storeTotalList(){
        StoreDto.StoreTotalRes result = storeService.storeTotalList();
        return ResponseEntity.ok(BaseResponse.success(result));
    }


    // 가맹점 목록 상세 조회
    @GetMapping("/detail/list/{storeIdx}")
    public ResponseEntity storeDetailList(@PathVariable Long storeIdx){
        StoreDto.StoreDetailListRes result = storeService.storeDetailList(storeIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 신규 가맹점 등록
    @PostMapping("/new/register")
    public ResponseEntity storeReg(@RequestBody StoreDto.StoreRegReq dto ){
        storeService.storeReg(dto);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    // S3 Pre-signed URL 요청
    @GetMapping("/presignedUrl/{fileName}")
    public ResponseEntity presignedUrl(@PathVariable(name = "fileName") String fileName){
        // 1. 서비스를 호출하여 URL과 고유 파일명을 받아옵니다.
        Map<String, String> result = storeService.getPresignedUrl(fileName);

        // 2. BaseResponse.success에 결과 Map을 담아 반환합니다.
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 가맹점 수정
    @PutMapping("/update/{storeIdx}")
    public ResponseEntity storeUpdate(@PathVariable(name = "storeIdx") Long storeIdx, @RequestBody StoreDto.StoreUpdateReq dto){
        storeService.storeUpdate(storeIdx, dto);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    // [가맹점] 로그인한 가맹점주의 월별 매출 정산 내역 조회
    @GetMapping("/income")
    public ResponseEntity<BaseResponse<StoreIncomeDto.MonthlyIncomeRes>> getMonthlyIncome(@AuthenticationPrincipal AuthUserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        StoreIncomeDto.MonthlyIncomeRes result = storeIncomeService.getMonthlyIncome(userDetails.getIdx());

        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
