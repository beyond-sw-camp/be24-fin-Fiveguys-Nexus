package com.example.nexus.domain.orders.model;

import lombok.Builder;

public class DangerDto {

    @Builder
    public static class DangerReq {
        private Integer ratio;
        private Integer period;

        public Danger toEntity() {
            return Danger.builder()
                    .ratio(this.ratio)
                    .period(this.period)
                    .build();
        }
    }
}
