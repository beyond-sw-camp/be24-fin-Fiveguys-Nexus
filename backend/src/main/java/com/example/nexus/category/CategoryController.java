package com.example.nexus.category;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/category")
@RequiredArgsConstructor
@RestController
public class CategoryController {
    private final CategoryService categoryService;
}
