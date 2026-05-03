package com.example.nexus.domain.menu.model;

import jakarta.persistence.*;

@Entity
public class MenuCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_category_idx")
    private Long idx;

    @Column(name = "menu_category_name", nullable = false, unique = true)
    private String menuCategoryName;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
