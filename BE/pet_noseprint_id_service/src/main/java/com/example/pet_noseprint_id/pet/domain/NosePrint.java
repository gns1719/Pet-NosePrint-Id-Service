package com.example.pet_noseprint_id.pet.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("NosePrint")
public class NosePrint {
    @Id
    private Long petId;         //펫 아이디
    private Long userKey;        //유저 아이디
    private String nosePrint;   //비문(url)
}
