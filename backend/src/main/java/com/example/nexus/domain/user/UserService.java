package com.example.nexus.domain.user;

import com.example.nexus.common.enums.Role;
import com.example.nexus.domain.user.model.AuthUserDetails;
import com.example.nexus.domain.user.model.User;
import com.example.nexus.domain.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserDto.SignupReq dto) {
        User user = dto.toEntity();
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }

    // store 회원가입
    public UserDto.StoreSignupRes storeSignup(UserDto.StoreSignupReq dto) {
        // 프론트에서 password를 보내는 경우, 그대로 사용한다.
        // (password가 비어있는 경우에만) 임시 비밀번호를 생성한다.
        String rawPassword = dto.getPassword();
        if (rawPassword == null || rawPassword.isBlank()) {
            int length = 12;
            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
            StringBuilder sb = new StringBuilder();
            SecureRandom secureRandom = new SecureRandom();
            for (int i = 0; i < length; i++) {
                int index = secureRandom.nextInt(characters.length());
                sb.append(characters.charAt(index));
            }
            rawPassword = sb.toString();
        }

        UserDto.StoreSignupReq storeSignupReq = UserDto.StoreSignupReq.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(rawPassword)
                .build();

        User user = storeSignupReq.toEntity();
        user.setPassword(passwordEncoder.encode(storeSignupReq.getPassword()));
        userRepository.save(user);

        return UserDto.StoreSignupRes.from(user, rawPassword);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow();

        return AuthUserDetails.from(user);
    }
}
