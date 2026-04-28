package com.example.nexus.domain.user.model;

import com.example.nexus.domain.user.model.User;
import lombok.Builder;
import lombok.Getter;

public class UserDto {

    @Getter
    public static class LoginReq {
        private String email;
        private String password;
    }

    @Builder
    @Getter
    public static class LoginRes {
        private Long idx;
        private String email;
        private String name;

        public static LoginRes from(User entity) {
            return LoginRes.builder()
                    .idx(entity.getIdx())
                    .email(entity.getEmail())
                    .name(entity.getUserName())
                    .build();
        }
    }
}
