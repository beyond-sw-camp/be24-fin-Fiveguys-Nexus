package com.example.nexus.domain.category.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class CategoryDto {
    @Getter
    public static class RegReq {
        @NotBlank(message = "Category name must not be blank")
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
