package com.example.nexus.domain.delivery;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/delivery")
@RestController
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;
}
