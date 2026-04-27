package com.example.nexus.storeproduct;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/storeproduct")
@RequiredArgsConstructor
@RestController
public class StoreProductController {
    private final StoreProductService storeProductService;
}
