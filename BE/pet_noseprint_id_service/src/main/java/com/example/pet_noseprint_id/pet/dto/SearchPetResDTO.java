package com.example.pet_noseprint_id.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchPetResDTO {

    private String name;        //펫 이름
    private LocalDate birth;    //펫 생일
    private String gender;      //펫 성별
    private String phoneNumber; //주인장 번호
}
