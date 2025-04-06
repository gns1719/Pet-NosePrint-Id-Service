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
    private Long userId;     // `User` 테이블의 `User_ID`를 참조
    private String provider; // OAuth 제공자 (Google, Kakao 등)
    private String providerKey; // OAuth 제공자가 부여한 고유 ID
}