package com.example.nexus.domain.product;

import com.example.nexus.common.model.BaseResponse;
import com.example.nexus.domain.product.model.ProductDto;
import com.example.nexus.domain.user.model.AuthUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<BaseResponse<ProductDto.RegRes>> addNewProduct(@Valid @RequestBody ProductDto.RegReq dto) {
        ProductDto.RegRes result = productService.addProduct(dto);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 제품 목록으로 조회
    @GetMapping("/list")
    public ResponseEntity<BaseResponse<ProductDto.ProductPageRes>> readProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoryName) {

        Pageable pageable = PageRequest.of(page, size);
        ProductDto.ProductPageRes result = productService.findAllProduct(pageable, categoryName);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 기존 제품 수정
    @PutMapping("/update")
    public ResponseEntity<BaseResponse<String>> updateProduct(@Valid @RequestBody ProductDto.RegReq dto) {
        boolean isModified = productService.updateProduct(dto.getIdx(), dto);

        if (isModified) {
            return ResponseEntity.ok(BaseResponse.success("success modify product"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.success("fail to modify: product or category not found"));
    }

    // 기존 제품 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<String>> deleteProduct(@RequestBody ProductDto.RegReq dto) {

        boolean isDeleted = productService.deleteProduct(dto.getIdx());

        if (isDeleted) {
            return ResponseEntity.ok(BaseResponse.success("success delete product"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.success("fail to delete: product not found"));
    }

    // 본사 제품 검색
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<ProductDto.ListRes>>> searchProduct(@RequestParam String productName) {
        List<ProductDto.ListRes> result = productService.searchProduct(productName);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 가맹점 별 제품 조회
    @GetMapping("/store")
    public ResponseEntity<BaseResponse<Page<ProductDto.ListRes>>> getMyStoreProducts(
            @AuthenticationPrincipal AuthUserDetails authUserDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (authUserDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        Pageable pageable = PageRequest.of(page, size);
        Long storeIdx = authUserDetails.getIdx();

        Page<ProductDto.ListRes> result = productService.findProductsByStore(storeIdx, pageable);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    // 가맹점 별 제품 검색
    @GetMapping("/store/search")
    public ResponseEntity<BaseResponse<List<ProductDto.ListRes>>> searchInMyStore(
            @AuthenticationPrincipal AuthUserDetails authUserDetails,
            @RequestParam String productName
    ) {
        if (authUserDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Long storeIdx = authUserDetails.getIdx();

        List<ProductDto.ListRes> result = productService.searchProductInStore(storeIdx, productName);

        return ResponseEntity.ok(BaseResponse.success(result));
    }
}
