package com.example.nexus.domain.menu.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.stream.Collectors;

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

    // 메뉴 페이징 조회
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
                    .menuList(entity.map(MenuDto.MenuListRes::from).getContent())
                    .totalPage(entity.getTotalPages())
                    .totalCount(entity.getTotalElements())
                    .currentPage(entity.getPageable().getPageNumber())
                    .currentSize(entity.getPageable().getPageSize())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ItemList{
        private Long idx;
        private String productName;
        private Integer quantity;
        private String menuUnit;


        public static ItemList from(MenuItem entity){
            return ItemList.builder()
                    .idx(entity.getIdx())
                    .productName(entity.getProduct().getProductName())
                    .quantity(entity.getQuantity())
                    .menuUnit(entity.getMenuUnit())
                    .build();
        }

    }

    // 메뉴 재료 조회
    @Getter
    @Builder
    public static class MenuItemListRes{
        private Long idx;
        private String menuName;
        private String menuCategory;
        private Integer price;
        private String imgPath;
        private List<ItemList> menuItemList;

        public static MenuDto.MenuItemListRes from(Menu entity){
            return MenuItemListRes.builder()
                    .idx(entity.getIdx())
                    .menuName(entity.getMenuName())
                    .menuCategory(entity.getMenuCategory().getMenuCategoryName())
                    .price(entity.getPrice())
                    .imgPath(entity.getImgPath())
                    .menuItemList(entity.getMenuItemList().stream()
                            .map(ItemList::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    // 카테고리 조회
    @Getter
    @Builder
    public static class MenuCategoryRes{
        private String menuCategoryName;

        public static MenuDto.MenuCategoryRes from(MenuCategory entity){
            return MenuCategoryRes.builder()
                    .menuCategoryName(entity.getMenuCategoryName())
                    .build();
        }
    }
}
