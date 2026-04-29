package com.example.nexus.config;

import com.example.nexus.domain.user.model.AuthUserDetails;
import com.example.nexus.domain.user.model.UserDto;
import com.example.nexus.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public LoginFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        AuthUserDetails user = (AuthUserDetails) authResult.getPrincipal();
        String token = JwtUtil.createToken(user.getIdx(), user.getUsername(), String.valueOf(user.getRole()));
        response.setHeader("Set-Cookie", "CTOKEN=" + token + "; Path=/");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.getWriter().write("login failed: " + failed.getMessage());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            UserDto.LoginReq dto = new ObjectMapper().readValue(request.getInputStream(), UserDto.LoginReq.class);

            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword(), null);

            return authenticationManager.authenticate(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}