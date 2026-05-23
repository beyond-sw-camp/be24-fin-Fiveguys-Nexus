package com.example.pos.event;

import java.util.List;

public record MenuEvent(
        Long menuIdx,
        String menuName,
        Integer price,
        String imgPath,
        Boolean isDeleted,
        String menuCategoryName,
        List<MenuItemEvent> menuItems
) {
    public record MenuItemEvent(
            Long idx,
            String menuUnit,
            Integer quantity,
            Long productIdx
    ){}
}
