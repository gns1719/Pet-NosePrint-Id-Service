package com.example.pet_noseprint_id.user.controller;

import com.example.pet_noseprint_id.user.domain.LocalUser;
import com.example.pet_noseprint_id.user.domain.User;
import com.example.pet_noseprint_id.user.dto.AddLocalUserRequestDTO;
import com.example.pet_noseprint_id.user.dto.LoginUserRequest;
import com.example.pet_noseprint_id.user.dto.LoginUserResponse;
import com.example.pet_noseprint_id.user.dto.ResponseDTO;
import com.example.pet_noseprint_id.user.service.LocalUserService;
import com.example.pet_noseprint_id.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final LocalUserService localUserService;
    private final PasswordEncoder passwordEncoder;

    // 이메일 중복 검사 API
    @GetMapping("/check-email")
    public  ResponseEntity<ResponseDTO<Long>> checkEmailDuplicate(@RequestParam String email) {
        userService.checkEmailDuplicate(email);

        ResponseDTO<Long> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User with email already exists");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 로컬 ID 중복 검사 API
    @GetMapping("/check-id")
    public ResponseEntity<ResponseDTO<Long>>  checkIdDuplicate(@RequestParam String id) {
        localUserService.isIdDuplicate(id);

        ResponseDTO<Long> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User with id already exists");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // 로컬 회원가입
    @PostMapping("/signup/local")
    public ResponseEntity<ResponseDTO<Long>> registerLocal(@RequestBody AddLocalUserRequestDTO request) {

        Long userId = userService.saveUser(
                new User(null,
                        "local",
                        request.getName(),
                        request.getPhoneNumber(),
                        LocalDate.now(),
                        request.getEmail()));

        localUserService.saveLocal(
                new LocalUser(userId,
                        request.getId(),
                        passwordEncoder.encode(request.getPassword()))
                );


        ResponseDTO<Long> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User signed up");
        response.setData(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/login/local")
    public ResponseEntity<ResponseDTO<LoginUserResponse>> login(@RequestBody LoginUserRequest request) {
        LoginUserResponse loginResponse = localUserService.login(request);
        ResponseDTO<LoginUserResponse> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User login successful.");
        response.setData(loginResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }


}
