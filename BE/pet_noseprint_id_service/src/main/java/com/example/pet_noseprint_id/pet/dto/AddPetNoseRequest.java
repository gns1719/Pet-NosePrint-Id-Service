package com.example.pet_noseprint_id.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddPetNoseRequest {
    private Long petId;
    private String nosePrint;
}
