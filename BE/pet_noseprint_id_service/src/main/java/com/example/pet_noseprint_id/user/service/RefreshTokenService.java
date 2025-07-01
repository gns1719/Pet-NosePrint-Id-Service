package com.example.pet_noseprint_id.user.service;

import com.example.pet_noseprint_id.user.redis.domain.RefreshToken;
import com.example.pet_noseprint_id.user.redis.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveTokenInfo(Long userKey, String refreshToken) {
        refreshTokenRepository.save(new RefreshToken(String.valueOf(userKey), refreshToken));
    }

    @Transactional
    public void removeRefreshToken(String userKey) {
        refreshTokenRepository.findById(userKey)
                .ifPresent(refreshToken -> refreshTokenRepository.delete(refreshToken));
    }

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
    }
}
