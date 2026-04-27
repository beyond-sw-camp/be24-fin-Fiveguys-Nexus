package com.example.nexus.menu.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="menu_idx", nullable = false, unique = true)
    private Long idx; // 메뉴 번호

    @Column(name = "menu_name", nullable = false, unique = true)
    private String menuName;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "img_path", nullable = false)
    private String imgPath;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

}
