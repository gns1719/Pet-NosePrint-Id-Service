package com.example.pet_noseprint_id.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PetUpdateDTO {
    private String name;        // 펫 이름
    private LocalDate birth;    // 펫 생일
    private String gender;      // 펫 성별
}