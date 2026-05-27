package com.example.nexus.domain.store;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.common.model.PageResponse;
import com.example.nexus.domain.store.model.StoreDto;
import com.example.nexus.domain.store.model.StoreIncomeDto;
import com.example.nexus.domain.store.model.StoreInventoryDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Tag(name = "가맹점 관리", description = "가맹점 정보 등록, 조회, 수정, 매출 정산 및 재고 관리 API")
@RequestMapping("/store")
@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final StoreIncomeService storeIncomeService;


    // [본사] 선택한 가맹점 store_idx로 재고 조회
    @Operation(summary = "가맹점별 재고 조회", description = "본사 관리자가 특정 가맹점의 재고 현황을 조회합니다.")
    @GetMapping("/inventory/list/{storeIdx}")
    public ResponseEntity<BaseResponse<PageResponse<StoreInventoryDto.ListRes>>> listByStoreIdx(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "가맹점 번호") @PathVariable Long storeIdx,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        PageResponse<StoreInventoryDto.ListRes> result = storeService.listByStoreIdxPaged(storeIdx, page, size);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // [본사] keyword로 가맹점 검색
    @Operation(summary = "가맹점 이름 검색", description = "가맹점 명칭(키워드)으로 가맹점을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity searchStore(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "검색 키워드") @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        StoreDto.StoreSearchReq reqDto = new StoreDto.StoreSearchReq(keyword);

        List<StoreDto.StoreSearchRes> result = storeService.searchByStoreName(reqDto);

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 가맹점 목록 조회 및 검색 조건
    @Operation(summary = "가맹점 목록 조회", description = "전체 가맹점 목록을 페이징하여 조회합니다. 상태 및 키워드 필터를 지원합니다.")
    @GetMapping("/list")
    public ResponseEntity storeList(
            StoreDto.StoreSearchPagingReq searchReq,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ){
        StoreDto.StorePageRes result = storeService.storeList(searchReq, page,size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(summary = "가맹점 전체 상태 집계", description = "현재 운영 중이거나 폐점된 가맹점의 전체 숫자를 집계하여 조회합니다.")
    @GetMapping("/totalCount/list")
    public ResponseEntity storeTotalList(){
        StoreDto.StoreTotalRes result = storeService.storeTotalList();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 월별 입점/폐점 추이 (연도 선택) - 본사 가맹점 관리 화면 차트용
    @Operation(summary = "월별 입점/폐점 추이 조회", description = "특정 연도의 월별 신규 입점 및 폐점 가맹점 추이 데이터를 조회합니다.")
    @GetMapping("/stats/monthly")
    public ResponseEntity<BaseResponse<StoreDto.MonthlyTrendRes>> monthlyTrend(
            @Parameter(description = "조회할 연도 (예: 2024). 미입력시 올해 기준") @RequestParam(required = false) Integer year){
        StoreDto.MonthlyTrendRes result = storeService.getMonthlyTrend(year);
        return ResponseEntity.ok(BaseResponse.success(result));
    }


    // 가맹점 목록 상세 조회
    @Operation(summary = "가맹점 상세 정보 조회", description = "특정 가맹점의 상세 정보(점주 명, 연락처, 주소 등)를 조회합니다.")
    @GetMapping("/detail/list/{storeIdx}")
    public ResponseEntity storeDetailList(@Parameter(description = "가맹점 번호") @PathVariable Long storeIdx){
        StoreDto.StoreDetailListRes result = storeService.storeDetailList(storeIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 신규 가맹점 등록
    @Operation(summary = "신규 가맹점 등록", description = "새로운 가맹점 정보를 등록합니다.")
    @PostMapping("/new/register")
    public ResponseEntity storeReg(@RequestBody StoreDto.StoreRegReq dto ){
        storeService.storeReg(dto);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    // S3 Pre-signed URL 요청
    @Operation(summary = "S3 업로드용 Pre-signed URL 생성", description = "가맹점 이미지 등을 S3에 직접 업로드하기 위한 Pre-signed URL을 생성합니다.")
    @GetMapping("/presignedUrl/{fileName}")
    public ResponseEntity presignedUrl(
            @Parameter(description = "업로드할 파일명 (확장자 포함)") @PathVariable(name = "fileName") String fileName,
            @Parameter(description = "파일 크기 (Byte 단위)", example = "1024") @RequestParam(name = "fileSize") long fileSize){
        // 1. 서비스를 호출하여 URL과 고유 파일명을 받아옵니다.
        Map<String, String> result = storeService.getPresignedUrl(fileName, fileSize);

        // 2. BaseResponse.success에 결과 Map을 담아 반환합니다.
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 가맹점 수정
    @Operation(summary = "가맹점 정보 수정", description = "기존 가맹점의 정보를 수정합니다.")
    @PutMapping("/update/{storeIdx}")
    public ResponseEntity storeUpdate(
            @Parameter(description = "가맹점 번호") @PathVariable(name = "storeIdx") Long storeIdx,
            @RequestBody StoreDto.StoreUpdateReq dto){
        storeService.storeUpdate(storeIdx, dto);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    // 가맹점별 매출 정산 조회
    @Operation(summary = "가맹점 매출 정산 조회", description = "로그인한 가맹점주가 특정 월의 매출 및 POS 정산 내역을 조회합니다.")
    @GetMapping("/income/settlement")
    public ResponseEntity<BaseResponse<StoreIncomeDto.TotalSettlementRes>> getSettlement(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUserDetails userDetails,
            @Parameter(description = "조회할 연도", example = "2024") @RequestParam int year,
            @Parameter(description = "조회할 월", example = "5") @RequestParam int month,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size
    ) {
        if (userDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }

        StoreIncomeDto.TotalSettlementRes result = storeIncomeService.getSettlementData(
                userDetails.getIdx(), year, month, page, size
        );

        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
