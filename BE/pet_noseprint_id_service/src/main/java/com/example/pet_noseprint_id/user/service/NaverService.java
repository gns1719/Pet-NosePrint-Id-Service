package com.example.pet_noseprint_id.user.service;

import com.example.pet_noseprint_id.user.dto.oauth.KakaoTokenResDTO;
import com.example.pet_noseprint_id.user.dto.oauth.KakaoUserInfoResDTO;
import com.example.pet_noseprint_id.user.dto.oauth.NaverTokenResDTO;
import com.example.pet_noseprint_id.user.dto.oauth.NaverUserInfoResDTO;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
public class NaverService {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private final String NAUTH_TOKEN_URL_HOST;
    private final String NAUTH_USER_URL_HOST;

    @Autowired
    public NaverService(@Value("${naver.client_id}") String clientId,
                        @Value("${naver.client_secret}")String clientSecret,
                        @Value("${naver.redirect_uri}") String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        NAUTH_TOKEN_URL_HOST ="https://nid.naver.com/oauth2.0";
        NAUTH_USER_URL_HOST = "https://openapi.naver.com/v1/nid/me";
    }

    public String getAccessTokenFromNaver(String code, String state) {

        NaverTokenResDTO NaverTokenResponseDto = WebClient.create(NAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("client_secret",clientSecret)
                        .queryParam("code", code)
                        .queryParam("state",state)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("4xx 오류 발생: " + errorBody)))
                )
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("5xx 서버 오류 발생: " + errorBody)))
                )
                .bodyToMono(NaverTokenResDTO.class)
                .block();


        log.info(" [Naver Service] Access Token ------> {}", NaverTokenResponseDto.getAccessToken());
        log.info(" [Naver Service] Refresh Token ------> {}", NaverTokenResponseDto.getRefreshToken());
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        log.info(" [Naver Service] ExpiresIn ------> {}", NaverTokenResponseDto.getExpiresIn());
        log.info(" [Naver Service] TokenType ------> {}", NaverTokenResponseDto.getTokenType());

        return NaverTokenResponseDto.getAccessToken();
    }


    public NaverUserInfoResDTO getUserInfo(String accessToken) {

        NaverUserInfoResDTO userInfo = WebClient.create(NAUTH_USER_URL_HOST)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(NaverUserInfoResDTO.class)
                .block();

        log.info("[ Naver Service ] Message ---> {} ", userInfo.getMessage());
        log.info("[ Naver Service ] Auth ID ---> {} ", userInfo.getResponse().getId());
        log.info("[ Naver Service ] NickName ---> {} ", userInfo.getResponse().getName());
        log.info("[ Naver Service ] Email ---> {} ", userInfo.getResponse().getEmail());
        log.info("[ Naver Service ] PhoneNumber ---> {} ", userInfo.getResponse().getMobile());

        return userInfo;
    }

    public String getUrl() {
        String state = UUID.randomUUID().toString(); // CSRF 방지용

        return "https://nid.naver.com/oauth2.0/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&state=" + state;
    }
}