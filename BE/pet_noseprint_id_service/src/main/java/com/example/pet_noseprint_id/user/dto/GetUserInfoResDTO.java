package com.example.pet_noseprint_id.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class GetUserInfoResDTO {
    private String name;
    private String phoneNumber;
    private String Email;
    private String loginType;
}
