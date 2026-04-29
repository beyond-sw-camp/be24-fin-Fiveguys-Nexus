package com.example.nexus.domain.product;

import com.example.nexus.domain.category.CategoryRepository;
import com.example.nexus.domain.category.model.Category;
import com.example.nexus.domain.product.model.Product;
import com.example.nexus.domain.product.model.ProductDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductDto.RegRes addProduct(ProductDto.RegReq dto) {
        Category category = categoryRepository.findById(dto.getCategoryIdx()).orElse(null);

        if (category != null) {
            Product product = dto.toEntity(category);
            Product saved = productRepository.save(product);
            return ProductDto.RegRes.from(saved);
        }
        return null;
    }
}
