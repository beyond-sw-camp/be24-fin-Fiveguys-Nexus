package com.example.nexus.domain.orders;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.orders.model.DangerDto;
import com.example.nexus.domain.orders.model.OrdersDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/orders")
@RestController
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersService orderService;

    @GetMapping("/list")
    public ResponseEntity list() {
        List<OrdersDto.OrdersRes> result = orderService.findAll();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @GetMapping("/detail/{ordersIdx}")
    public ResponseEntity ordersDetail(@PathVariable Long ordersIdx) {
        OrdersDto.OrdersRes result = orderService.findId(ordersIdx);
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

}
