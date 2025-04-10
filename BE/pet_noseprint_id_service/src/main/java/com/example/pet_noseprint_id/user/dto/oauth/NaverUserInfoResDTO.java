package com.example.pet_noseprint_id.user.dto.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;

@Getter
@NoArgsConstructor //역직렬화를 위한 기본 생성자
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserInfoResDTO {

    @JsonProperty("resultcode")
    private String resultCode;

    @JsonProperty("message")
    private String message;

    @JsonProperty("response")
    private NaverResponse response;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NaverResponse {

        @JsonProperty("id")
        private String id;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("name")
        private String name;

        @JsonProperty("email")
        private String email;

        @JsonProperty("gender")
        private String gender; // F: 여성, M: 남성, U: 확인불가

        @JsonProperty("age")
        private String age;

        @JsonProperty("birthday")
        private String birthday; // MM-DD 형식

        @JsonProperty("profile_image")
        private String profileImage;

        @JsonProperty("birthyear")
        private String birthYear;

        @JsonProperty("mobile")
        private String mobile;
    }
}
