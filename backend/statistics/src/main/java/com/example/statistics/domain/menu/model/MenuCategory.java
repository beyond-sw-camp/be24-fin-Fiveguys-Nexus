package com.example.statistics.domain.menu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menu_category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategory {
    @Id
    @Column(name = "menu_category_idx")
    private Long idx;

    @Column(name = "menu_category_name", nullable = false)
    private String menuCategoryName;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}