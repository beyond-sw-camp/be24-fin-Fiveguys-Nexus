package com.example.nexus.domain.menu;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.menu.model.MenuDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/menu")
@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    // 메뉴 리스트 조회
    @GetMapping("/list")
    public ResponseEntity list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        MenuDto.MenuPageRes result =  menuService.list(page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }


    // 메뉴 재료 조회
    @GetMapping("/item/list/{menuIdx}")
    public ResponseEntity menuItemList(@PathVariable Long menuIdx){
        MenuDto.MenuItemListRes result = menuService.menuItemList(menuIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
