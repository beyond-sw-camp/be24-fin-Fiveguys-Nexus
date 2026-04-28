package com.example.nexus.domain.category.model;

import lombok.Builder;
import lombok.Getter;

public class CategoryDto {
    @Getter
    public static class RegReq {
        private String categoryName;

        public Category toEntity() {
            return Category.builder()
                    .categoryName(this.categoryName)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class RegRes {
        private Long idx;
        private String categoryName;

        public static RegRes from(Category entity) {
            return RegRes.builder()
                    .idx(entity.getIdx())
                    .categoryName(entity.getCategoryName())
                    .build();
        }
    }
}
