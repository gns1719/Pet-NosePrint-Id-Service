package com.example.pet_noseprint_id.user.config.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtProvider jwtProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/users/oauth/login") || path.startsWith("/users/local/login")
                || path.startsWith("/users/oauth/signup") || path.startsWith("/users/local/signup")
                || path.startsWith("/v3/") || path.startsWith("/docs")
                || path.startsWith("/static/") || path.equals("/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        System.out.print("JWT Filter Start");

        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String token = getAccessToken(authorizationHeader);
//        LOGGER.info("[doFilterInternal] 토큰 추출 완료. token: {}", token);

        if (token != null && jwtProvider.validateToken(token)) {
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
//            LOGGER.info("[doFilterInternal] 토큰 유효성 검사 완료");
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
//        LOGGER.info("[resolveToken] HTTP 헤더에서 토큰 값 추출");
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
//            return authorizationHeader.substring(TOKEN_PREFIX.length());
            return authorizationHeader.split(" ")[1];
        }

        return null;
    }
}