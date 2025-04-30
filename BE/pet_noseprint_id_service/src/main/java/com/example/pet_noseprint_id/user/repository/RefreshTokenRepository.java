package com.example.pet_noseprint_id.user.repository;

import com.example.pet_noseprint_id.user.redis.RefreshToken;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken,String> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    Optional<RefreshToken> findById(String Id);
}