package com.example.nexus.domain.user;

import com.example.nexus.domain.user.model.AuthUserDetails;
import com.example.nexus.domain.user.model.UserDto;
import com.example.nexus.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody UserDto.SignupReq dto) {
        userService.signup(dto);
        return ResponseEntity.ok("성공");
    }

    @PostMapping("/store/signup")
    public ResponseEntity storeSignup(@RequestBody UserDto.StoreSignupReq dto) {
        
        return ResponseEntity.ok("성공");
    }

//    @PostMapping("/login")
//    public ResponseEntity login(@RequestBody UserDto.LoginReq dto) {
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword(), null);
//
//        Authentication authentication = authenticationManager.authenticate(token);
//        AuthUserDetails user = (AuthUserDetails) authentication.getPrincipal();
//
//        if (user != null) {
//            String jwt = JwtUtil.createToken(user.getIdx(), user.getUsername(), user.getRole().toString());
//            return ResponseEntity.ok().header("Set-Cookie", "CTOKEN=" + jwt + "; Path=/").build();
//        }
//
//        return ResponseEntity.ok("로그인 실패");
//
//    }

}
