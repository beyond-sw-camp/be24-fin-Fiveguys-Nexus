package com.example.nexus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
@Configuration
public class AuthenticationManagerConfig {


    @Bean
    public AuthenticationManager authenticationmanager(UserDetailsService userDetailsService) {



    }

}
