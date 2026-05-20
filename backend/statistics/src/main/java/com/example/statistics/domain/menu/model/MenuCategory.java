package com.example.statistics.domain.menu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLInsert;

@Entity
@Table(name = "menu_category")
@SQLInsert(sql =                                        // ← 추가
        "INSERT INTO menu_category (menu_category_idx, menu_category_name, is_deleted) " +
        "VALUES (?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE " +
        "menu_category_name = VALUES(menu_category_name), " +
        "is_deleted = VALUES(is_deleted)")
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