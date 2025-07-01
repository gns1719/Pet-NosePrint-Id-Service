package com.example.pet_noseprint_id.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FindUserDTO {
    private String dogName;     //펫 이름
    private LocalDate birth;    // 펫 생일 -> 나이로 변환해주기
    private String gender;      // 펫 성별
    private String profile;     // 펫 프로필 사진 URL
    private String name;        //사람 이름
    private String phoneNumber; //사람 전화번호

}
