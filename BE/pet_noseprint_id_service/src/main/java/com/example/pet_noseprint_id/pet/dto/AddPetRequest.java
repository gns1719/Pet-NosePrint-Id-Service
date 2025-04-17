package com.example.pet_noseprint_id.pet.dto;

import com.example.pet_noseprint_id.pet.domain.Pet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddPetRequest {
    private String name;        // 펫 이름
    private LocalDate birth;    // 펫 생일
    private String gender;      // 펫 성별
    private String profile;     // 펫 프로필 사진 URL
}

