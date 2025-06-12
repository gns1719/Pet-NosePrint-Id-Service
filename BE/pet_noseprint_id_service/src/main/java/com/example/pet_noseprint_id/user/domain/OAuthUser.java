package com.example.pet_noseprint_id.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table("OAuth_User")
public class OAuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth_key")
    private Long oauthKey;  // 새로운 PK

    @Column(name = "user_key", nullable = false, unique = true)
    private Long userKey;   // `User` 테이블의 FK (Unique)

    @Column(nullable = false)
    private String provider;  // "google" 또는 "kakao"

    @Column(name = "provider_key", nullable = false)
    private String providerKey;  // 제공자 고유 ID
}