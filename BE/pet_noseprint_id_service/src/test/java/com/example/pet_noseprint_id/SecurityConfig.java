package com.example.pet_noseprint_id;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (테스트용)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/**").permitAll() // 회원가입, 로그인 API는 모두 허용
                        .anyRequest().authenticated()             // 나머지는 인증 필요
                )

                .formLogin(Customizer.withDefaults()) // ✅ 기본 formLogin 설정, 또는 완전히 제거 가능
                .httpBasic(Customizer.withDefaults()); // 필요 없다면 이 라인도 제거 가능

        return http.build();
    }
}
