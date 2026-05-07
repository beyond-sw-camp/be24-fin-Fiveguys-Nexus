package com.example.nexus.domain.category;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.category.model.CategoryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/category")
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    // 카테고리 등록
    @PostMapping("/reg")
    public ResponseEntity<BaseResponse<CategoryDto.RegRes>> createCategory(@Valid @RequestBody CategoryDto.RegReq dto) {
        CategoryDto.RegRes result = categoryService.addCategory(dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 카테고리 목록 조회
    @GetMapping("/list")
    public ResponseEntity readCategoryList() {
        List<CategoryDto.ListRes> dto = categoryService.findAllCategories();
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    // 카테고리 소프트 삭제
    @DeleteMapping("/delete/{idx}")
    public ResponseEntity deleteCategory(@PathVariable Long idx) {
        categoryService.deleteCategory(idx);
        return ResponseEntity.ok(BaseResponse.success("success delete category"));
    }
}
