package com.example.pet_noseprint_id.user.controller;

import com.example.pet_noseprint_id.user.domain.LocalUser;
import com.example.pet_noseprint_id.user.domain.User;
import com.example.pet_noseprint_id.user.dto.*;
import com.example.pet_noseprint_id.user.service.LocalUserService;
import com.example.pet_noseprint_id.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequestMapping("/users/local")
@RequiredArgsConstructor
@RestController
@Slf4j
public class LocalUserController {
    private final UserService userService;
    private final LocalUserService localUserService;
    private final PasswordEncoder passwordEncoder;

    // 로컬 ID 중복 검사 API
    @GetMapping("/check-id")
    public ResponseEntity<ResponseDTO<Long>>  checkIdDuplicate(@RequestParam String id) {
        localUserService.isIdDuplicate(id);

        ResponseDTO<Long> response = new ResponseDTO<>();
        response.setStatus(true);

        response.setMessage("사용 가능한 아이디 입니다.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 로컬 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO<Long>> registerLocal(@RequestBody CreateAccessTokenReqDTO.AddLocalUserReqDTO request) {

        Long userId = userService.saveUser(
                new User(null,
                        "local",
                        request.getName(),
                        request.getPhoneNumber(),
                        LocalDate.now(),
                        request.getEmail()));

        localUserService.saveLocal(
                new LocalUser(null,userId,
                        request.getId(),
                        passwordEncoder.encode(request.getPassword()))
        );


        ResponseDTO<Long> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User signed up");
        response.setData(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<LoginUserResDTO>> login(@RequestBody LoginUserReqDTO request) {
        LoginUserResDTO loginResponse = localUserService.login(request);
        ResponseDTO<LoginUserResDTO> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User login successful.");
        response.setData(loginResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    // 비밀번호 찾기
    @PostMapping("/find-pw")
    public ResponseEntity<ResponseDTO<?>> findPw(@RequestBody FindPasswordReqDTO request) {
        userService.findPassword(request.getUserId());

        ResponseDTO<LoginUserResDTO> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("임시 비밀번호가 이메일로 전송되었습니다.");

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }


}
