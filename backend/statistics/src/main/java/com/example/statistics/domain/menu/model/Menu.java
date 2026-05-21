package com.example.statistics.domain.menu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLInsert;

@Entity
@Table(name = "menu")
@SQLInsert(sql =
        "INSERT INTO menu (menu_idx, menu_name, price, is_deleted, menu_category_idx) " +
        "VALUES (?, ?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE " +
        "menu_name = VALUES(menu_name), " +
        "price = VALUES(price), " +
        "is_deleted = VALUES(is_deleted), " +
        "menu_category_idx = VALUES(menu_category_idx)")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    @Id
    @Column(name = "menu_idx")
    private Long idx;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_category_idx")
    private MenuCategory menuCategory;
}