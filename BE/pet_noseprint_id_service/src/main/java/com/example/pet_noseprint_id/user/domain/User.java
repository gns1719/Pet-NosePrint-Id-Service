package com.example.pet_noseprint_id.user.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("User")
public class User {
    @Id
    private Long userId;            // 자동 증가 PK
    private String loginType;       // 로그인 타입
    private String name;            // 사용자 이름
    private String phoneNumber;     // 전화번호
    private LocalDate createDate;   // 가입 날짜
    private String email;           // 이메일 (Unique)
}