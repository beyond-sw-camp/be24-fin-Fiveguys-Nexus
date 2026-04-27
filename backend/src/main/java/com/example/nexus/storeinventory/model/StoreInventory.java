package com.example.nexus.storeinventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "store_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_inventory_idx")
    private Long idx;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StoreInventoryStatus status = StoreInventoryStatus.NORMAL;

    @Column(name = "manufactured_date", nullable = false)
    private LocalDateTime manufacturedDate;

    @Column(name = "avg_stock", nullable = false)
    private Integer avgStock;

    @Column(name = "store_idx", nullable = false)
    private Long storeIdx;

    @Column(name = "product_idx", nullable = false)
    private Long productIdx;
}
