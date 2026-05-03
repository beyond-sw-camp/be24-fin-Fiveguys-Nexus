package com.example.nexus.domain.menu.model;

import lombok.Builder;
import lombok.Getter;

public class MenuDto {

    @Builder
    @Getter
    public static class MenuListRes{
        private Long idx;
        private String menuName;
        private Integer price;
        private String menuCategory;
        private Integer menuItemCount;


        public static MenuDto.MenuListRes from(Menu entity){
            return MenuListRes.builder()
                    .idx(entity.getIdx())
                    .menuName(entity.getMenuName())
                    .price(entity.getPrice())
                    .menuCategory(entity.getMenuCategory().getMenuCategoryName())
                    .menuItemCount(entity.getMenuItemList().size())
                    .build();
        }
    }
}
