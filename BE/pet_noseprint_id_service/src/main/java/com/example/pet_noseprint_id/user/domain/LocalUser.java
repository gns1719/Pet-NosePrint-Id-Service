package com.example.pet_noseprint_id.user.domain;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Local_User") // 테이블 이름이 실제 DB와 다를 경우 name 명시
public class LocalUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "local_key")
    private Long localKey; // PK

    @Column(name = "user_key", nullable = false)
    private Long userKey; // FK (User 테이블 참조)

    @Column(nullable = false, unique = true)
    private String id; // 로그인 ID

    @Column(nullable = false)
    private String password; // 비밀번호
}