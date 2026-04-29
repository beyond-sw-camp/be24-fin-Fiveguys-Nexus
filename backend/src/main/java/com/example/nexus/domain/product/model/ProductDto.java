package com.example.nexus.domain.product.model;

import com.example.nexus.domain.category.model.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class ProductDto {
    @Getter
    public static class RegReq {
        private String productName;
        private Long categoryIdx;
        private String productUnit;
        private Integer maxStock;
        private Integer minStock;
        private Integer unitPrice;
        private String dangerDays;

        public Product toEntity(Category category) {
            return Product.builder()
                    .productName(this.productName)
                    .category(category)
                    .productUnit(this.productUnit)
                    .maxStock(this.maxStock)
                    .minStock(this.minStock)
                    .unitPrice(this.unitPrice)
                    .dangerDays(this.dangerDays)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class RegRes {
        private Long idx;
        private String productName;

        public static RegRes from(Product entity) {
            return RegRes.builder()
                    .idx(entity.getIdx())
                    .productName(entity.getProductName())
                    .build();
        }
    }


}
