package com.example.nexus.domain.orders.model;

import lombok.Builder;
import lombok.Getter;

public class DangerDto {

    @Getter
    @Builder
    public static class DangerReq {
        private Integer ratio;
        private Integer period;
    }

    @Getter
    @Builder
    public static class DangerRes {
        private Integer ratio;
        private Integer period;

        public static DangerRes from(Danger entity) {
            return DangerRes.builder()
                    .ratio(entity.getRatio())
                    .period(entity.getPeriod())
                    .build();
        }
    }
}
