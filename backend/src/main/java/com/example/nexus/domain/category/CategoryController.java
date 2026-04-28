package com.example.nexus.domain.category;

import com.example.nexus.domain.category.model.CategoryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/category")
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/reg")
    public ResponseEntity register(@RequestBody CategoryDto.RegReq dto) {
        CategoryDto.RegRes result = categoryService.register(dto);
        return ResponseEntity.ok(result);
    }
}
