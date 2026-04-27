package com.example.nexus.menuitem;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/MenuItem")
@RestController
@RequiredArgsConstructor
public class MenuItemController {
    private final MenuItemService menuItemService;
}
