package com.example.pet_noseprint_id.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ChangePasswordReqDTO {
    private String currentPassword;
    private String newPassword;
}
