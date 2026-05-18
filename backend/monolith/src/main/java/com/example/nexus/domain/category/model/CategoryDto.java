package com.example.nexus.domain.category.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CategoryDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegReq {
        private Long idx;
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

    @Getter
    @Builder
    public static class ListRes {
        private Long idx;
        private String categoryName;

        public static ListRes from(Category entity) {
            return ListRes.builder()
                    .idx(entity.getIdx())
                    .categoryName(entity.getCategoryName())
                    .build();
        }
    }
}
