package com.example.pet_noseprint_id.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddLocalUserRequestDTO {
    private String name;
    private String phoneNumber;
    private String email;
    private String id;
    private String password;
}