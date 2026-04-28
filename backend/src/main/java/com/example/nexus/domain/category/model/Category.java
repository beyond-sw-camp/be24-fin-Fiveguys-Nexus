package com.example.nexus.domain.category.model;

import com.example.nexus.domain.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_idx")
    private Long idx;

    @Column(name = "category_name", nullable = false)
    private Long categoryName;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @OneToMany
    private List<Product> productList;
}
