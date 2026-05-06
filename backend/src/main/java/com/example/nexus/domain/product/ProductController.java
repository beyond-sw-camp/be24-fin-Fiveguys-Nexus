package com.example.nexus.domain.product;

import com.example.nexus.domain.product.model.ProductDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    // 기존 제품 수정
    @PutMapping("/update/{idx}")
    public ResponseEntity<String> updateProduct(@PathVariable Long idx, @RequestBody ProductDto.RegReq dto) {
        boolean isModified = productService.updateProduct(idx, dto);
        if (isModified) {
            return ResponseEntity.ok("success modify product");
        }
        return ResponseEntity.badRequest().body("fail to modify: product or category not found");
    }

    @DeleteMapping("/delete/{idx}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long idx) {
        boolean isDeleted = productService.deleteProduct(idx);
        if (isDeleted) {
            return ResponseEntity.ok("success delete product");
        }
        return ResponseEntity.badRequest().body("fail to delete: product not found");
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto.ListRes>> searchProduct(@RequestParam String productName) {
        List<ProductDto.ListRes> result = productService.searchProduct(productName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/store")
    public ResponseEntity<List<ProductDto.ListRes>> getMyStoreProducts(
            @AuthenticationPrincipal AuthUserDetails authUserDetails
    ) {
        if (authUserDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Long storeIdx = authUserDetails.getIdx();

        List<ProductDto.ListRes> list = productService.findProductsByStore(storeIdx);
        return ResponseEntity.ok(list);
    }
}
