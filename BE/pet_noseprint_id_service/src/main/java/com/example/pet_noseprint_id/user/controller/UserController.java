package com.example.pet_noseprint_id.user.controller;

import com.example.pet_noseprint_id.user.dto.ResponseDTO;
import com.example.pet_noseprint_id.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

    private final UserService userService;

    // 이메일 중복 검사 API
    @GetMapping("/check-email")
    public  ResponseEntity<ResponseDTO<Long>> checkEmailDuplicate(@RequestParam String email) {
        userService.checkEmailDuplicate(email);

        ResponseDTO<Long> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User with email already exists");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }




}
