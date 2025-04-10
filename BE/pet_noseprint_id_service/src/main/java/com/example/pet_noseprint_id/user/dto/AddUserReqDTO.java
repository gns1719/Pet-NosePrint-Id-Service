package com.example.pet_noseprint_id.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddUserRequestDTO {
    private String email;
    private String name;
    private String phone;
}