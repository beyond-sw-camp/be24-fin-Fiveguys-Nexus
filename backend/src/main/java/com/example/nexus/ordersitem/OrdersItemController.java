package com.example.nexus.ordersitem;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/orderitem")
@RestController
@RequiredArgsConstructor
public class OrdersItemController {
    private final OrdersItemService orderItemService;
}
