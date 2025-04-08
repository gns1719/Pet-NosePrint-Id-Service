package com.example.pet_noseprint_id.user.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("OAuth_User")
public class OAuthUser {

    @Id
    private Long oauthKey;     // 새로운 PK
    private Long userKey;          // `User` 테이블의 FK (Unique)
    private String provider;      // "google" 또는 "kakao"
    private String providerKey;   // 제공자 고유 ID
}