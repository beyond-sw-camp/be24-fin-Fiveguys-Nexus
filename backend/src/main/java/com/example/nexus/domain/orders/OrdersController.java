package com.example.nexus.domain.orders;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.orders.model.DangerDto;
import com.example.nexus.domain.orders.model.OrdersDto;
import com.example.nexus.domain.orders.model.OrdersItemDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@RequestMapping("/orders")
@RestController
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersService orderService;

    @GetMapping("/list/auto")
    public ResponseEntity autoList() {
        List<OrdersDto.OrderListRes> result = orderService.findAllAuto();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/list/manual")
    public ResponseEntity manualList() {
        List<OrdersDto.OrderListRes> result = orderService.findAllManual();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PostMapping
    public ResponseEntity create(@RequestBody OrdersDto.OrdersReq req) {
        orderService.create(req);
        return ResponseEntity.ok(BaseResponse.success("create success"));
    }

    @GetMapping("/list")
    public ResponseEntity list() {
        List<OrdersDto.OrdersRes> result = orderService.findAll();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/{ordersIdx}")
    public ResponseEntity ordersDetail(@PathVariable Long ordersIdx) {
        OrdersDto.OrdersRes result = orderService.findById(ordersIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/danger")
    public ResponseEntity find() {
        DangerDto.DangerRes result = orderService.find();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @PutMapping("/danger")
    public ResponseEntity save(@RequestBody DangerDto.DangerReq req) {
        orderService.save(req);
        return ResponseEntity.ok(BaseResponse.success("update success"));
    }

    @PutMapping("/{ordersIdx}/approve")
    public ResponseEntity approve(@PathVariable Long ordersIdx) {
        orderService.approve(ordersIdx);
        return ResponseEntity.ok(BaseResponse.success("update success"));
    }

    @PutMapping("/{ordersIdx}/reject")
    public ResponseEntity reject(@PathVariable Long ordersIdx) {
        orderService.reject(ordersIdx);
        return ResponseEntity.ok(BaseResponse.success("update success"));
    }

    @PostMapping("/{ordersIdx}/items")
    public ResponseEntity addItem(@PathVariable Long ordersIdx, @RequestBody OrdersItemDto.OrdersItemReq req) {
        orderService.addItem(ordersIdx, req);
        return ResponseEntity.ok(BaseResponse.success("create success"));
    }

    @PutMapping("/items/{ordersItemIdx}")
    public ResponseEntity updateItemCount(@PathVariable Long ordersItemIdx, @RequestBody OrdersItemDto.OrdersItemReq req) {
        orderService.updateItemCount(ordersItemIdx, req.getCount());
        return ResponseEntity.ok(BaseResponse.success("update success"));
    }

    @DeleteMapping("/items/{ordersItemIdx}")
    public ResponseEntity deleteItem(@PathVariable Long ordersItemIdx) {
        orderService.deleteItem(ordersItemIdx);
        return ResponseEntity.ok(BaseResponse.success("delete success"));
    }

    @GetMapping("/store/list")
    public ResponseEntity storeList(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        if (authUserDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }
        List<OrdersDto.OrdersRes> result = orderService.findByUserIdx(authUserDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/store/find")
    public ResponseEntity storeFind(@AuthenticationPrincipal AuthUserDetails authUserDetails) {
        if (authUserDetails == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "로그인이 필요합니다.");
        }
        List<OrdersDto.OrdersRes> result = orderService.findByUserIdxAndOrdersStatus(authUserDetails.getIdx());
        return ResponseEntity.ok(BaseResponse.success(result));
    }

}
