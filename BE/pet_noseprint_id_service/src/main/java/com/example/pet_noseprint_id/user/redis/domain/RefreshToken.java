package com.example.pet_noseprint_id.user.redis.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@AllArgsConstructor
@Getter
@RedisHash(value = "Token", timeToLive = 60*60*24*3)
public class RefreshToken {

    @Id
    private String id;

    private String refreshToken;


}