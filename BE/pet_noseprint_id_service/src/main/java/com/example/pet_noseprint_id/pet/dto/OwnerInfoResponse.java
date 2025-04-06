package com.example.pet_noseprint_id.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerInfoResponse {
    private String name;            // 사용자 이름
    private String phoneNumber;     // 전화번호
    private LocalDate createDate;   // 가입 날짜
    private String email;           // 이메일 (Unique)
}