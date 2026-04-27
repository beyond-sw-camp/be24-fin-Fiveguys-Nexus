package com.example.nexus.menuitem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_item_idx", nullable = false, unique = true)
    private Long idx;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "menu_unit", nullable = false)
    private String menuUnit;

}
