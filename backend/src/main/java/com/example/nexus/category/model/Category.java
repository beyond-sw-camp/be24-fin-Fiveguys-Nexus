package com.example.nexus.category.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_idx", nullable = false)
    private Long idx;

    @Column(name = "category_name", nullable = false)
    private Long categoryName;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;
}
