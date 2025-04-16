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

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users/oauth")
public class OAuthUserController {

    private final OAuthLoginService oAuthLoginService;

    // <editor-fold desc="카카오">

    @Value("${kakao.client_id}")
    private String kakao_client_id;

    @Value("${kakao.redirect_uri}")
    private String kakao_redirect_uri;





    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        LoginUserResDTO loginResponse = oAuthLoginService.handleKakao(code);

        if (isTestMode()) {
            String redirectUrl = String.format(
                    "/users/oauth/login-success?accessToken=%s&refreshToken=%s&userId=%d",
                        loginResponse.getAccessToken(),
                    loginResponse.getRefreshToken(),
                    loginResponse.getUserId()
            );

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }


        ResponseDTO<LoginUserResDTO> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User login successful.");
        response.setData(loginResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // </editor-fold>


    // <editor-fold desc="구글">


    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {
        LoginUserResDTO loginResponse = oAuthLoginService.handleGoogle(code);

        if (isTestMode()) {
            String redirectUrl = String.format(
                    "/users/oauth/login-success?accessToken=%s&refreshToken=%s&userId=%d",
                    loginResponse.getAccessToken(),
                    loginResponse.getRefreshToken(),
                    loginResponse.getUserId()
            );

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }


        ResponseDTO<LoginUserResDTO> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User login successful.");
        response.setData(loginResponse);

        return ResponseEntity.ok(response);
    }

    // </editor-fold>


    // <editor-fold desc="네이버">

    @Value("${naver.client_id}")
    private String naver_client_id;

    @Value("${naver.redirect_uri}")
    private String naver_redirect_uri;

    String uuid_state = UUID.randomUUID().toString();


    @GetMapping("/naver/callback")
    public ResponseEntity<?> naverCallback(@RequestParam("code") String code,
                                           @RequestParam("state") String state) {

        LoginUserResDTO loginResponse = oAuthLoginService.handleNaver(code, state);

        if (isTestMode()) {
            String redirectUrl = String.format(
                    "/users/oauth/login-success?accessToken=%s&refreshToken=%s&userId=%d",
                    loginResponse.getAccessToken(),
                    loginResponse.getRefreshToken(),
                    loginResponse.getUserId()
            );

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();
        }


        ResponseDTO<LoginUserResDTO> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User login successful.");
        response.setData(loginResponse);

        return ResponseEntity.ok(response);
    }

    // </editor-fold>



    // <editor-fold desc="로컬 환경 테스트용">

    @GetMapping("/page")
    public String loginPage(Model model) {

        String kakao_location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+ kakao_client_id +"&redirect_uri="+ kakao_redirect_uri;
        String naver_location = "https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id="+ naver_client_id+"&state="+ uuid_state +"&redirect_uri="+naver_redirect_uri;

        model.addAttribute("kakao_location", kakao_location);
        model.addAttribute("naver_location", naver_location);


        return "login";
    }

    @GetMapping("/login-success")
    public String loginSuccessPage(@RequestParam String accessToken,
                                   @RequestParam String refreshToken,
                                   @RequestParam Long userId,
                                   Model model) {

        model.addAttribute("accessToken", accessToken);
        model.addAttribute("refreshToken", refreshToken);
        model.addAttribute("userId", userId);

        return "login-success"; // templates/login-success.html
    }

    private boolean isTestMode() {
        return false; // 나중에 application.yml에서 설정값으로 바꿔도 OK
    }
    // </editor-fold>

}
