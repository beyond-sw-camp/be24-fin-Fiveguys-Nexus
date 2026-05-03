package com.example.nexus.domain.menu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_category")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_category_idx")
    private Long idx;

    @Column(name = "menu_category_name", nullable = false, unique = true)
    private String menuCategoryName;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
