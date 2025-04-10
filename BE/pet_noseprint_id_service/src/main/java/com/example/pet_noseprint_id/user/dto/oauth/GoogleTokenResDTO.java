package com.example.pet_noseprint_id.user.dto.oauth;

import lombok.Data;

@Data
public class GoogleTokenResDTO {
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String scope;
    private String token_type;
    private String id_token;
}