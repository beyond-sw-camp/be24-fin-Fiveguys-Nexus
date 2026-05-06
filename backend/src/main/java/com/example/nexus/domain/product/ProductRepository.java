package com.example.nexus.domain.product;

import com.example.nexus.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductNameContaining(String productName);

    @Query("SELECT p FROM Product p " +
            "JOIN StoreInventory s ON s.product.idx = p.idx " +
            "WHERE s.store.idx = :storeIdx")
    List<Product> findAllByStoreIdx(@Param("storeIdx") Long storeIdx);
}
