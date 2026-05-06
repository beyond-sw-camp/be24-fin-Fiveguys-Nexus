package com.example.nexus.domain.menu.model;

import com.example.nexus.common.exception.BaseException;
import com.example.nexus.domain.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.nexus.common.model.BaseResponseStatus.*;

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

    @Getter
    public static class MenuItemReq{
        private Integer quantity;
        private String menuUnit;
        private Long productIdx;

        public MenuItem toEntity(Menu menu, Product product) {
            return MenuItem.builder()
                    .quantity(this.quantity)
                    .menuUnit(this.menuUnit)
                    .product(product) // 조회된 진짜 객체 주입
                    .menu(menu)
                    .build();
        }
    }

    // 메뉴 등록
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuRegReq{
        private String menuName;
        private Integer price;
        private String imgPath;
        private Long menuCategoryIdx;
        private List<MenuItemReq> menuItemList;

        public Menu toEntity(MenuCategory category, List<Product> products) {
            Menu menu = Menu.builder()
                    .menuName(this.menuName)
                    .price(this.price)
                    .imgPath(this.imgPath)
                    .menuCategory(category) // 주입받은 카테고리 설정
                    .build();

            if (this.menuItemList != null) {
                Map<Long, Product> productMap = products.stream()
                        .collect(Collectors.toMap(Product::getIdx, p -> p));

                for (MenuItemReq itemDto : this.menuItemList) {
                    Product product = productMap.get(itemDto.getProductIdx());
                    if (product == null) {
                        throw new BaseException(NOT_FOUND_PRODUCT);
                    }

                    // 1. MenuItem 생성 시 'menu' 객체를 넘겨주어 자식에게 부모를 알려줌
                    MenuItem menuItem = itemDto.toEntity(menu, product);

                    // 2. 부모 객체의 리스트에도 자식을 추가 (양방향 연결)
                    menu.getMenuItemList().add(menuItem);
                }
            }
            return menu;
        }
    }
}
