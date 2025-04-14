package com.example.pet_noseprint_id.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseDTO<T> {
    private boolean status;
    private String message;
    private T data;

    public ResponseDTO(boolean status, String message) {
        this.status = status;
        this.message = message;
    }
}