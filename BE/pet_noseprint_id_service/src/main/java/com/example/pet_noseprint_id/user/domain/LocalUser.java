package com.example.pet_noseprint_id.user.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("Local_User")
public class LocalUser {
    @Id
    private Long userId;  // `User` 테이블의 `User_ID`를 참조
    private String id;    // 로그인 ID (Unique)
    private String password;    // 비밀번호
}
