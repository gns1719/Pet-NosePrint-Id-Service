package com.example.pet_noseprint_id.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table("OAuth_User")
public class OAuthUserEntity {
    @Id
    private Long userId;
    private String provider;
    private String providerKey;
}