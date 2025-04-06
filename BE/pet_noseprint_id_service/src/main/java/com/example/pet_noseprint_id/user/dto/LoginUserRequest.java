package com.example.pet_noseprint_id.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserRequest {
    private Long id;
    private String password;
}
