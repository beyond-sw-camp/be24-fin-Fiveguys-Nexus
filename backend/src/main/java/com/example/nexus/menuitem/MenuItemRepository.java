package com.example.nexus.menuitem;

import com.example.nexus.menuitem.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
