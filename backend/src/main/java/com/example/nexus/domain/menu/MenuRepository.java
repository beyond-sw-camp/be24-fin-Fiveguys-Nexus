package com.example.nexus.domain.menu;

import com.example.nexus.domain.menu.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    boolean existsByMenuName(String menuName);
}
