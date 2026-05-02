package com.example.nexus.domain.orders;

import com.example.nexus.common.enums.OrdersType;
import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.common.model.PageResponse;
import com.example.nexus.domain.orders.model.DangerDto;
import com.example.nexus.domain.orders.model.OrdersDto;
import com.example.nexus.domain.orders.model.OrdersItemDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@RequestMapping("/orders")
@RestController
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersService orderService;

    /**
     * 자동 발주 목록 조회
     *
     * @param keyword 검색 키워드 (선택)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return ResponseEntity 자동 발주 목록 (페이징)
     */
    @GetMapping("/list/auto")
    public ResponseEntity autoList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrdersDto.OrderListRes> result = orderService.findAllAuto(keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(BaseResponse.success(PageResponse.from(result)));
    }

    /**
     * 확정 발주 목록 조회
     *
     * @param keyword 검색 키워드 (선택)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return ResponseEntity 확정 발주 목록 (페이징)
     */
    @GetMapping("/list/confirmed")
    public ResponseEntity confirmedList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrdersDto.OrderListRes> result = orderService.findAllConfirmed(
                keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(BaseResponse.success(PageResponse.from(result)));
    }

    /**
     * 발주 이력 목록 조회
     *
     * @param ordersType 발주 유형 필터 (선택)
     * @param startDate 조회 시작일 (선택)
     * @param endDate 조회 종료일 (선택)
     * @param keyword 검색 키워드 (선택)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return ResponseEntity 발주 이력 목록 (페이징)
     */
    @GetMapping("/list/history")
    public ResponseEntity historyList(
            @RequestParam(required = false) OrdersType ordersType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrdersDto.OrderListRes> result = orderService.findOrderHistory(
                ordersType, startDate, endDate, keyword,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(BaseResponse.success(PageResponse.from(result)));
    }

    /**
     * 위험 발주 목록 조회
     *
     * @param startDate 조회 시작일 (선택)
     * @param endDate 조회 종료일 (선택)
     * @param keyword 검색 키워드 (선택)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return ResponseEntity 위험 발주 목록 (페이징)
     */
    @GetMapping("/list/danger")
    public ResponseEntity dangerList(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DangerDto.DangerListRes> result = orderService.findDangerOrders(
                startDate, endDate, keyword,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(BaseResponse.success(PageResponse.from(result)));
    }

    /**
     * 발주 상세 정보 조회
     *
     * @param ordersIdx 조회할 발주의 고유 ID
     * @return ResponseEntity 발주 상세 정보
     */
    @GetMapping("/{ordersIdx}")
    public ResponseEntity ordersDetail(@PathVariable Long ordersIdx) {
        OrdersDto.OrdersRes result = orderService.findById(ordersIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 위험 발주 기준 설정 조회
     *
     * @return ResponseEntity 위험 발주 기준 설정 정보
     */
    @GetMapping("/danger")
    public ResponseEntity find() {
        DangerDto.DangerRes result = orderService.find();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 위험 발주 기준 설정 수정
     *
     * @param req 위험 발주 기준 설정 요청 데이터
     * @return ResponseEntity 수정 결과 메시지
     */
    @PutMapping("/danger")
    public ResponseEntity save(@RequestBody DangerDto.DangerReq req) {
        orderService.save(req);
        return ResponseEntity.ok(BaseResponse.success("update success"));
    }

    /**
     * 발주 승인
     *
     * @param ordersIdx 승인할 발주의 고유 ID
     * @return ResponseEntity 승인 결과 메시지
     */
    @PutMapping("/{ordersIdx}/approve")
    public ResponseEntity approve(@PathVariable Long ordersIdx) {
        orderService.approve(ordersIdx);
        return ResponseEntity.ok(BaseResponse.success("update success"));
    }

    /**
     * 발주 반려
     *
     * @param ordersIdx 반려할 발주의 고유 ID
     * @return ResponseEntity 반려 결과 메시지
     */
    @PutMapping("/{ordersIdx}/reject")
    public ResponseEntity reject(@PathVariable Long ordersIdx) {
        orderService.reject(ordersIdx);
        return ResponseEntity.ok(BaseResponse.success("update success"));
    }

    /**
     * 발주 항목 추가
     *
     * @param ordersIdx 항목을 추가할 발주의 고유 ID
     * @param req 추가할 발주 항목 요청 데이터
     * @return ResponseEntity 추가 결과 메시지
     */
    @PostMapping("/{ordersIdx}/items")
    public ResponseEntity addItem(@PathVariable Long ordersIdx, @RequestBody OrdersItemDto.OrdersItemReq req) {
        orderService.addItem(ordersIdx, req);
        return ResponseEntity.ok(BaseResponse.success("create success"));
    }

    /**
     * 발주 항목 수량 수정
     *
     * @param ordersItemIdx 수정할 발주 항목의 고유 ID
     * @param req 수정할 수량 요청 데이터
     * @return ResponseEntity 수정 결과 메시지
     */
    @PutMapping("/items/{ordersItemIdx}")
    public ResponseEntity updateItemCount(@PathVariable Long ordersItemIdx, @RequestBody OrdersItemDto.OrdersItemReq req) {
        orderService.updateItemCount(ordersItemIdx, req.getCount());
        return ResponseEntity.ok(BaseResponse.success("update success"));
    }

    /**
     * 발주 항목 삭제
     *
     * @param ordersItemIdx 삭제할 발주 항목의 고유 ID
     * @return ResponseEntity 삭제 결과 메시지
     */
    @DeleteMapping("/items/{ordersItemIdx}")
    public ResponseEntity deleteItem(@PathVariable Long ordersItemIdx) {
        orderService.deleteItem(ordersItemIdx);
        return ResponseEntity.ok(BaseResponse.success("delete success"));
    }

    /**
     * 확정 발주 일괄 승인
     *
     * @return ResponseEntity 일괄 승인 결과 메시지
     */
    @PutMapping("/confirmed/approve")
    public ResponseEntity approveAll() {
        orderService.approveAllConfirmed();
        return ResponseEntity.ok(BaseResponse.success("update success"));
    }

    /**
     * 발주 취소
     *
     * @param ordersIdx 취소할 발주의 고유 ID
     * @return ResponseEntity 취소 결과 메시지
     */
    @PutMapping("/{ordersIdx}/cancel")
    public ResponseEntity cancel(@PathVariable Long ordersIdx) {
        orderService.cancelOrder(ordersIdx);
        return ResponseEntity.ok(BaseResponse.success("update success"));
    }

    /**
     * 가맹점 수동 발주 생성
     *
     * @param authUserDetails 인증된 사용자 정보
     * @param req 수동 발주 요청 데이터
     * @return ResponseEntity 생성 결과 메시지
     * @throws ResponseStatusException 로그인하지 않은 경우 (401)
     */
    @PostMapping("/store/reg/manual")
    public ResponseEntity createStoreManualOrder(@AuthenticationPrincipal AuthUserDetails authUserDetails, @RequestBody OrdersDto.OrdersReq req) {
        if (authUserDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }
        orderService.createStoreManualOrder(authUserDetails.getIdx(), req);
        return ResponseEntity.ok(BaseResponse.success("create success"));
    }

    /**
     * 가맹점 발주 항목 추가
     *
     * @param authUserDetails 인증된 사용자 정보
     * @param ordersIdx 항목을 추가할 발주의 고유 ID
     * @param req 추가할 발주 항목 요청 데이터
     * @return ResponseEntity 추가 결과 메시지
     * @throws ResponseStatusException 로그인하지 않은 경우 (401)
     */
    @PostMapping("/store/{ordersIdx}/items")
    public ResponseEntity addStoreItem(
            @AuthenticationPrincipal AuthUserDetails authUserDetails,
            @PathVariable Long ordersIdx,
            @RequestBody OrdersItemDto.OrdersItemReq req
    ) {
        if (authUserDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }
        orderService.addStoreItem(authUserDetails.getIdx(), ordersIdx, req);
        return ResponseEntity.ok(BaseResponse.success("create success"));
    }

    /**
     * 가맹점 발주 항목 수량 수정
     *
     * @param authUserDetails 인증된 사용자 정보
     * @param ordersItemIdx 수정할 발주 항목의 고유 ID
     * @param req 수정할 수량 요청 데이터
     * @return ResponseEntity 수정 결과 메시지
     * @throws ResponseStatusException 로그인하지 않은 경우 (401)
     */
    @PutMapping("/store/{ordersItemIdx}/items")
    public ResponseEntity updateStoreItemCount(
            @AuthenticationPrincipal AuthUserDetails authUserDetails,
            @PathVariable Long ordersItemIdx,
            @RequestBody OrdersItemDto.OrdersItemReq req
    ) {
        if (authUserDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }
        orderService.updateStoreItemCount(authUserDetails.getIdx(), ordersItemIdx, req.getCount());
        return ResponseEntity.ok(BaseResponse.success("update success"));
    }

    /**
     * 가맹점 발주 목록 조회
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return ResponseEntity 가맹점 발주 목록
     * @throws ResponseStatusException 로그인하지 않은 경우 (401)
     */
    @GetMapping("/store/list")
    public ResponseEntity storeList(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        if (authUserDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }
        List<OrdersDto.OrdersRes> result = orderService.findByUserIdx(authUserDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 가맹점 진행 중인 발주 조회
     *
     * @param authUserDetails 인증된 사용자 정보
     * @return ResponseEntity 가맹점 진행 중인 발주 목록
     * @throws ResponseStatusException 로그인하지 않은 경우 (401)
     */
    @GetMapping("/store/find")
    public ResponseEntity storeFind(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        if (authUserDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }
        List<OrdersDto.OrdersRes> result = orderService.findByUserIdxAndOrdersStatus(authUserDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

}
