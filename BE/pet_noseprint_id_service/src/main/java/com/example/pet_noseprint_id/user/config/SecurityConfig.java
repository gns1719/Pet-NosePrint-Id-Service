package com.example.pet_noseprint_id.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/docs/**", "/h2-console/**").permitAll() // 여기에 공개하고 싶은 경로 추가
                        .anyRequest().permitAll() // 나머지도 인증 없이 접근
                )
                .formLogin(AbstractHttpConfigurer::disable) // 로그인 폼 제거
                .httpBasic(AbstractHttpConfigurer::disable);// HTTP Basic 인증 제거
        return http.build();
    }
}