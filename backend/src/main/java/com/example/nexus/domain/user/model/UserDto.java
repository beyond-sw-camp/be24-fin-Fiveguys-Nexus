package com.example.nexus.domain.user.model;

import com.example.nexus.common.enums.Role;
import com.example.nexus.domain.user.model.User;
import lombok.Builder;
import lombok.Getter;

public class UserDto {

    @Getter
    public static class SignupReq {
        private String email;
        private String name;
        private String password;
        private Role role;
        private String tel;

        public User toEntity() {
            return User.builder()
                    .email(this.email)
                    .userName(this.name)
                    .password(this.password)
                    .role(this.role)
                    .tel(this.tel)
                    .isDeleted(false)
                    .build();
        }
    }



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

    @Getter
    public static class StoreSignupReq {
        private String email;
    }


    public static class StoreSignupRes {
        private String email;
        private String password;
    }

}
