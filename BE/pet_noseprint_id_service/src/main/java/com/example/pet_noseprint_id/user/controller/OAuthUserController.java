package com.example.pet_noseprint_id.user.controller;

import com.example.pet_noseprint_id.user.dto.ResponseDTO;
import com.example.pet_noseprint_id.user.dto.LoginUserResDTO;
import com.example.pet_noseprint_id.user.service.OAuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users/oauth")
public class OAuthUserController {

    private final OAuthLoginService oAuthLoginService;

    // <editor-fold desc="카카오">




    @GetMapping("/kakao/url")
    public RedirectView getKakaoUrl() {
        String loginUrl = oAuthLoginService.getLoginUrl("kakao");
        return new RedirectView(loginUrl);
    }


    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        LoginUserResDTO loginResponse = oAuthLoginService.handleKakao(code);



        ResponseDTO<LoginUserResDTO> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User login successful.");
        response.setData(loginResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // </editor-fold>


    // <editor-fold desc="구글">

    @GetMapping("/google/url")
    public RedirectView getGoogleUrl() {
        String loginUrl = oAuthLoginService.getLoginUrl("google");
        return new RedirectView(loginUrl);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {
        LoginUserResDTO loginResponse = oAuthLoginService.handleGoogle(code);


        ResponseDTO<LoginUserResDTO> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User login successful.");
        response.setData(loginResponse);

        return ResponseEntity.ok(response);
    }

    // </editor-fold>


    // <editor-fold desc="네이버">
    @GetMapping("/naver/url")
    public RedirectView getNaverUrl() {
        String loginUrl = oAuthLoginService.getLoginUrl("naver");
        return new RedirectView(loginUrl);
    }

    @GetMapping("/naver/callback")
    public ResponseEntity<?> naverCallback(@RequestParam("code") String code,
                                           @RequestParam("state") String state) {

        LoginUserResDTO loginResponse = oAuthLoginService.handleNaver(code, state);



        ResponseDTO<LoginUserResDTO> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User login successful.");
        response.setData(loginResponse);

        return ResponseEntity.ok(response);
    }

    // </editor-fold>





}
