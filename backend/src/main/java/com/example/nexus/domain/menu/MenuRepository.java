package com.example.nexus.domain.menu;

import com.example.nexus.domain.menu.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
