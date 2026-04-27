package com.example.nexus.storeproduct;


import com.example.nexus.storeproduct.model.StoreProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreProductRepository extends JpaRepository<StoreProduct, Long> {
}
