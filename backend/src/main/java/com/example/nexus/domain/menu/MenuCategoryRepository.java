package com.example.nexus.domain.menu;

import com.example.nexus.domain.menu.model.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {
}
