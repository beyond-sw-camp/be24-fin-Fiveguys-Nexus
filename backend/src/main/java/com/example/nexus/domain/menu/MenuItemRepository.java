package com.example.nexus.domain.menu;

import com.example.nexus.domain.menu.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
