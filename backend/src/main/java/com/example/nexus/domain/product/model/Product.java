package com.example.nexus.domain.product.model;


import com.example.nexus.domain.category.model.Category;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_idx")
    private Long idx;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_unit", nullable = false)
    private String productUnit;

    @Column(name = "max_stock", nullable = false)
    private Integer maxStock;

    @Column(name = "min_stock", nullable = false)
    private Integer minStock;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(name = "danger_days", nullable = false)
    private String dangerDays;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_idx", nullable = false)
    private Category category;

}
