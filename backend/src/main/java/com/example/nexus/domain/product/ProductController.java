package com.example.nexus.domain.product;

import com.example.nexus.domain.product.model.ProductDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/product")
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    // 신규 제품 등록
    @PostMapping("/reg")
    public ResponseEntity<ProductDto.RegRes> addNewProduct(@Valid @RequestBody ProductDto.RegReq dto) {
        ProductDto.RegRes result = productService.addProduct(dto);
        return ResponseEntity.ok(result);
    }
}
