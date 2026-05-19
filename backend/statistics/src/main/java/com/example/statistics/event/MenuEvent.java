package com.example.statistics.event;

public record MenuEvent(
        Long menuIdx,
        String menuName,
        Integer price,
        Long menuCategoryIdx,
        String menuCategoryName,
        Boolean isDeleted
) {}