package com.example.pet_noseprint_id.pet.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("Pet")
public class Pet {
    @Id
    private Long petId;         //펫 아이디
    private Long ownerId;        //유저 아이디
    private String name;        //펫 이름
    private LocalDate birth;    //펫 생일
    private String gender;      //펫 성별
    private String profile;     //펫 프로필 사진(url)
}
