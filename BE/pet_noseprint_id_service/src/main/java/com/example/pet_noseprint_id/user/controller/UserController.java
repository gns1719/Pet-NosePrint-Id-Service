package com.example.pet_noseprint_id.member.controller;

import com.example.pet_noseprint_id.member.dto.ResponseDTO;
import com.example.pet_noseprint_id.member.dto.member.AddUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/member")
@RequiredArgsConstructor
@RestController
public class MemberController {

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO<Long>> register(@RequestBody AddUserRequest request) {
        Long savedId = userService.save(request);
        ResponseDTO<Long> response = new ResponseDTO<>();
        response.setStatus(true);
        response.setMessage("User registration successful.");
        response.setData(savedId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
