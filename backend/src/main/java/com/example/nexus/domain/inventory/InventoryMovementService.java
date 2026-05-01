package com.example.nexus.domain.inventory;

import com.example.nexus.common.enums.InventoryStatus;
import com.example.nexus.domain.head.HeadInventoryRepository;
import com.example.nexus.domain.head.model.HeadInventory;
import com.example.nexus.domain.inventory.model.InventoryMovement;
import com.example.nexus.domain.inventory.model.InventoryMovementDto;
import com.example.nexus.domain.product.ProductRepository;
import com.example.nexus.domain.product.model.Product;
import com.example.nexus.domain.store.StoreInventoryRepository;
import com.example.nexus.domain.store.StoreRepository;
import com.example.nexus.domain.store.model.Store;
import com.example.nexus.domain.store.model.StoreInventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class InventoryMovementService {
    private final InventoryMovementRepository inventoryMovementRepository;
    private final HeadInventoryRepository headInventoryRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public InventoryMovementDto.MovementRes inbound(InventoryMovementDto.InboundReq req) {

        Product product = productRepository.findById(req.getProductIdx()).orElseThrow();

        HeadInventory headInventory = headInventoryRepository.findByProductIdx(product.getIdx()).orElseThrow();

        headInventory.setCount(headInventory.getCount() + req.getQuantity());

        HeadInventory savedHeadInventory = headInventoryRepository.save(headInventory);

        InventoryMovement movement = InventoryMovement.inbound(
                product,
                req.getQuantity(),
                req.getMemo()
        );
        InventoryMovement savedMovement = inventoryMovementRepository.save(movement);

        return InventoryMovementDto.MovementRes.builder()
                .movementIdx(savedMovement.getIdx())
                .productIdx(product.getIdx())
                .quantity(req.getQuantity())
                .headCount(savedHeadInventory.getCount())
                .build();
    }
}
