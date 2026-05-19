package com.example.statistics.domain.menu;

import com.example.statistics.domain.menu.model.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
}