package com.example.nexus.storeproduct;

import com.example.nexus.storeinventory.StoreInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreProductService {
    private final StoreProductRepository storeProductRepository;
}
