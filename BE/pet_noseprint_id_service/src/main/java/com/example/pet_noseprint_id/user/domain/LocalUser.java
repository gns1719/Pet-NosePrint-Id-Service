package com.example.pet_noseprint_id.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("Local_User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalUserEntity {
    @Id
    private Long userId;
    private String id;
    private String pw;
    private String salt;

}
