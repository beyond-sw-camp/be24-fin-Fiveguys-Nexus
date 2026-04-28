package com.example.nexus.domain.category;

import com.example.nexus.domain.category.model.Category;
import com.example.nexus.domain.category.model.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryDto.RegRes register(CategoryDto.RegReq dto) {
        Category entity = categoryRepository.save(dto.toEntity());
        return CategoryDto.RegRes.from(entity);
    }

    public List<CategoryDto.ListRes> findAllCategories() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream().map(CategoryDto.ListRes::from).toList();
    }
}
