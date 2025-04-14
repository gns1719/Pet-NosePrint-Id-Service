package com.example.pet_noseprint_id.user.config.jwt;

import com.example.pet_noseprint_id.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class JwtProvider {

    private final Logger LOGGER = Logger.getLogger(JwtProvider.class.getName());

    @Value("${spring.jwt.access-secret}")
    private String accessSecretKey;
    @Value("${spring.jwt.refresh-secret}")
    private String refreshSecretKey;

    // Access Token 생성
    public String createAccessToken(Long userId, Long expireTimeMs) {
        //LOGGER.info("[createToken] Access 토큰 생성 시작");
        String token = Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)
                .compact();

        //LOGGER.info("[createToken] Access 토큰 생성 완료");
        return token;
        }

    // Refresh Token 생성
    public String createRefreshToken(Long userId, Long expireTimeMs) {
        //LOGGER.info("[createToken] Refresh 토큰 생성 시작");
        String token = Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
                .compact();

        //LOGGER.info("[createToken] Refresh 토큰 생성 완료");
        return token;
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(accessSecretKey)
                    .build()
                    .parseEncryptedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());

        } catch (Exception e) {
            LOGGER.info("[validateToken] Error: " + e.getMessage());
            return false;
        }
    }


    // 토큰으로부터 인증 정보 조회
    public Authentication getAuthentication(String token) {
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
                getUserId(token),
                token,
                authorities
        );
    }

    public String getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("userId", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(accessSecretKey).build()
                .parseSignedClaims(token)
                .getBody();
    }
}
