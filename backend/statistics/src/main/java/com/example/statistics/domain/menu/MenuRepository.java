package com.example.statistics.domain.menu;

import com.example.statistics.domain.menu.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}