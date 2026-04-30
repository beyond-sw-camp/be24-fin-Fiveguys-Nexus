package com.example.nexus.domain.product;

import com.example.nexus.domain.category.CategoryRepository;
import com.example.nexus.domain.category.model.Category;
import com.example.nexus.domain.product.model.Product;
import com.example.nexus.domain.product.model.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional(readOnly = true)
    public List<ProductDto.ListRes> findAllProduct() {
        List<Product> entities = productRepository.findAll();
        List<ProductDto.ListRes> dtos = new ArrayList<>();

        for (Product entity : entities) {
            dtos.add(ProductDto.ListRes.from(entity));
        }
        return dtos;
    }

    @Transactional
    public boolean updateProduct(Long idx, ProductDto.RegReq dto) {
        Product product = productRepository.findById(idx).orElse(null);
        if (product == null) return false;

        Category category = categoryRepository.findById(dto.getCategoryIdx()).orElse(null);
        if (category == null) return false;

        product.update(
                dto.getProductName(),
                dto.getProductUnit(),
                dto.getMaxStock(),
                dto.getMinStock(),
                dto.getUnitPrice(),
                dto.getDangerDays(),
                category
        );
        return true;
    }

    @Transactional
    public boolean deleteProduct(Long idx) {
        Product product = productRepository.findById(idx).orElse(null);

        if (product != null) {
            productRepository.delete(product);
            return true;
        }
        return false;
    }
}
