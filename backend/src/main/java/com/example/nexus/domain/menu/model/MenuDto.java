package com.example.nexus.domain.menu.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

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

    @Getter
    @Builder
    public static class MenuPageRes{
        private List<MenuListRes> menuList;
        private int totalPage;
        private long totalCount;
        private int currentPage;
        private int currentSize;

        public static MenuPageRes from(Page<Menu> entity){
            return MenuPageRes.builder()
                    .menuList(entity.get().map(MenuDto.MenuListRes::from).toList())
                    .totalPage(entity.getTotalPages())
                    .totalCount(entity.getTotalElements())
                    .currentPage(entity.getPageable().getPageNumber())
                    .currentSize(entity.getPageable().getPageSize())
                    .build();
        }
    }
}
