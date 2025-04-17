package com.example.pet_noseprint_id.user.service;

import com.example.pet_noseprint_id.user.dto.oauth.GoogleTokenResDTO;
import com.example.pet_noseprint_id.user.dto.oauth.GoogleUserInfoResDTO;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect_uri}")
    private String redirectUri;

    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com";
    private final String GOOGLE_USERINFO_URL = "https://www.googleapis.com";

    public String getAccessTokenFromGoogle(String code) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);
        formData.add("grant_type", "authorization_code");

        GoogleTokenResDTO tokenResponse = WebClient.builder()
                .baseUrl(GOOGLE_TOKEN_URL)
                .build()
                .post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("4xx 오류 발생: " + errorBody)))
                )
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("5xx 오류 발생: " + errorBody)))
                )
                .bodyToMono(GoogleTokenResDTO.class)
                .block();

        log.info("[Google Service] Access Token ------> {}", tokenResponse.getAccess_token());
        log.info("[Google Service] Refresh Token ------> {}", tokenResponse.getRefresh_token());
        log.info("[Google Service] Id Token ------> {}", tokenResponse.getId_token());

        return tokenResponse.getAccess_token();
    }

    public GoogleUserInfoResDTO getUserInfo(String accessToken) {
        GoogleUserInfoResDTO userInfo = WebClient.create(GOOGLE_USERINFO_URL)
                .get()
                .uri("/oauth2/v2/userinfo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("4xx 오류 발생: " + errorBody))))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("5xx 오류 발생: " + errorBody))))
                .bodyToMono(GoogleUserInfoResDTO.class)
                .block();

        log.info("[Google Service] ID ---> {}", userInfo.getId());
        log.info("[Google Service] Email ---> {}", userInfo.getEmail());
        log.info("[Google Service] Name ---> {}", userInfo.getName());
        log.info("[Google Service] Picture ---> {}", userInfo.getPicture());

        return userInfo;
    }

    public String getUrl() {
        String state = UUID.randomUUID().toString(); // CSRF 방지용
        String scope = "email%20profile";

        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=" + scope
                + "&access_type=offline"
                + "&state=" + state;
    }
}