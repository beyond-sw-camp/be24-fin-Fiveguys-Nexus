package com.example.pos.domain.menu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_idx")
    private Long idx;

    @Column(name = "menu_name", nullable = false, unique = true)
    private String menuName;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "img_path", nullable = false)
    private String imgPath;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MenuItem> menuItemList = new ArrayList<>();

    @Column(name = "menu_category_name",  nullable = false)
    private String menuCategoryName;

}
