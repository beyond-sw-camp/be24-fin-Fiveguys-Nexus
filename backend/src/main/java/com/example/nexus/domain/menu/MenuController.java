package com.example.nexus.domain.menu;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.menu.model.MenuDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RequestMapping("/menu")
@RestController
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    // 메뉴 리스트 조회
    @GetMapping("/list")
    public ResponseEntity list(
            MenuDto.MenuSearchPagingReq searchReq,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        MenuDto.MenuPageRes result =  menuService.list(searchReq, page, size);
        return ResponseEntity.ok(BaseResponse.success(result));
    }


    // 메뉴 재료 조회
    @GetMapping("/item/list/{menuIdx}")
    public ResponseEntity menuItemList(@PathVariable Long menuIdx){
        MenuDto.MenuItemListRes result = menuService.menuItemList(menuIdx);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // S3 Pre-signed URL 요청
    @GetMapping("/presignedUrl/{fileName}")
    public ResponseEntity presignedUrl(@PathVariable(name = "fileName") String fileName){
        // 1. 서비스를 호출하여 URL과 고유 파일명을 받아옵니다.
        Map<String, String> result = menuService.getPresignedUrl(fileName);

        // 2. BaseResponse.success에 결과 Map을 담아 반환합니다.
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 메뉴 등록
    @PostMapping("/new/register")
    public ResponseEntity menuRegister(@Valid @RequestBody MenuDto.MenuRegReq dto){
        menuService.menuRegister(dto);

        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    // 메뉴 수정
    @PutMapping("/update/{menuIdx}")
    public ResponseEntity menuUpdate(@PathVariable(name = "menuIdx") Long menuIdx, @Valid  @RequestBody MenuDto.MenuRegReq dto){
        menuService.menuUpdate(menuIdx, dto);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    // 메뉴 삭제
    @PutMapping("/delete/{menuIdx}")
    public ResponseEntity menuDelete(@PathVariable(name = "menuIdx") Long menuIdx){
        menuService.menuDelete(menuIdx);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    // 메뉴 카테고리 조회
    @GetMapping("/category/list")
    public ResponseEntity menuCategory(){
        List<MenuDto.MenuCategoryRes> result =  menuService.menucategory();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 메뉴 카테고리 등록
    @PostMapping("/category/register")
    public ResponseEntity<BaseResponse> menuCategoryReg(@Valid @RequestBody MenuDto.MenuCategoryRegReq dto){
        menuService.menucategoryReg(dto);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }

    // 메뉴 카테고리 삭제
    @DeleteMapping("/category/delete/{categoryIdx}")
    public ResponseEntity menuCategoryDelete(@PathVariable Long categoryIdx ){
        menuService.menuCategoryDelete(categoryIdx);
        return ResponseEntity.ok(BaseResponse.success("성공"));
    }
}
