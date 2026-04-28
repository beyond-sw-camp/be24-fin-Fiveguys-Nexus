package com.example.nexus.domain.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/menu")
@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
}
