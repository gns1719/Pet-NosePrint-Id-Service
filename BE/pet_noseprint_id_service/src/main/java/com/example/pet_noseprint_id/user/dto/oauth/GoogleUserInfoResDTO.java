package com.example.pet_noseprint_id.user.dto.oauth;

import lombok.Data;

@Data
public class GoogleUserInfoResDTO {
    private String id;
    private String email;
    private String verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String locale;
}