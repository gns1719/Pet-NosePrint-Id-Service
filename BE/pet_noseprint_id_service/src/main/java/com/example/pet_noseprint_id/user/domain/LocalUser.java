package com.example.pet_noseprint_id.user.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("Local_User")
public class LocalUser {

    @Id
    private Long localKey;  // 새로운 PK
    private Long userKey;       // FK (User 테이블 참조)
    private String id;         // 로그인 ID
    private String password;   // 비밀번호
}