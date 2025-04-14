package com.example.pet_noseprint_id.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class CreateAccessTokenReqDTO {
    private String refreshToken;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddLocalUserReqDTO {
        private String name;
        private String phoneNumber;
        private String email;
        private String id;
        private String password;
    }
}