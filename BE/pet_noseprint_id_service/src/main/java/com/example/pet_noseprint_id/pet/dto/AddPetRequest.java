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
    private Long ownerId;        // 주인의 ID (서버 측에서 주입)(나중에 토큰에서 추출로 변경)
    private String name;        // 펫 이름
    private LocalDate birth;    // 펫 생일
    private String gender;      // 펫 성별
    private String profile;     // 펫 프로필 사진 URL
}

