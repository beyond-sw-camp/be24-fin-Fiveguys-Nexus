package com.example.nexus.domain.orders;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/order")
@RestController
@RequiredArgsConstructor
public class OrdersController {
    private final OrdersService orderService;

}
