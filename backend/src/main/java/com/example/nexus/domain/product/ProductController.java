package com.example.nexus.domain.product;

import com.example.nexus.domain.product.model.ProductDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 제품 목록으로 조회
    @GetMapping("/list")
    public ResponseEntity<List<ProductDto.ListRes>> readProductList () {
        List<ProductDto.ListRes> list = productService.findAllProduct();
        return ResponseEntity.ok(list);
    }
}
