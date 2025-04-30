package com.example.pet_noseprint_id.user.controller;

import com.example.pet_noseprint_id.user.dto.*;
import com.example.pet_noseprint_id.user.service.EmailService;
import com.example.pet_noseprint_id.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    // 이메일 중복 검사 API
    @GetMapping("/check-email")
    public  ResponseEntity<ResponseDTO<Long>> checkEmailDuplicate(@RequestParam String email) {
        userService.checkEmailDuplicate(email);

        ResponseDTO<Long> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("이미 사용중인 이메일 입니다.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // 비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<ResponseDTO<?>> changePassword(@AuthenticationPrincipal Long userKey,
                                                 @RequestBody ChangePasswordReqDTO request) {
        userService.changePassword(userKey, request.getCurrentPassword(), request.getNewPassword());

        ResponseDTO<Long> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("비밀번호가 성공적으로 변경되었습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //회원정보 수정
    @PostMapping("/update-profile")
    public ResponseEntity<ResponseDTO<?>> updateProfile(@AuthenticationPrincipal Long userKey,
                                                        @RequestBody UpdateUserInfoReqDTO request) {
        userService.updateUserInfo(userKey, request.getName(), request.getPhoneNumber(), request.getEmail());

        ResponseDTO<Long> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("회원정보가 성공적으로 수정되었습니다.");
        return ResponseEntity.ok(response);
    }
}
