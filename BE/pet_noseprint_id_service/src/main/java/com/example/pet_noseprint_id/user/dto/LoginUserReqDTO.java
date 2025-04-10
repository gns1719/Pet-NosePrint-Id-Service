package com.example.pet_noseprint_id.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserReqDTO {
    private String id;
    private String password;
}
