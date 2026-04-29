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


        int length = 12;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        String randomPassword = sb.toString();

        UserDto.StoreSignupReq storeSignupReq = UserDto.StoreSignupReq.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(randomPassword)
                .build();

        User user = storeSignupReq.toEntity();
        System.out.println(user.getPassword());
        user.setPassword(passwordEncoder.encode(storeSignupReq.getPassword()));

        userRepository.save(storeSignupReq.toEntity());

        UserDto.StoreSignupRes storeSignupRes = UserDto.StoreSignupRes.from(storeSignupReq.toEntity(), randomPassword);
        


        return storeSignupRes;

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow();

        return AuthUserDetails.from(user);
    }
}
