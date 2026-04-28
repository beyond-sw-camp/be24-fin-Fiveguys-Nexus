package com.example.nexus.domain.store;


import com.example.nexus.domain.store.model.StoreProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreProductRepository extends JpaRepository<StoreProduct, Long> {
}
