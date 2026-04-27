package com.example.nexus.orderitem;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/orderitem")
@RestController
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;
}
