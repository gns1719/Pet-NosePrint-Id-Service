package com.example.pet_noseprint_id.member.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    public Long save(AddUserRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new IllegalArgumentException("아이디 중복");
                });

        return userRepository.save(User.builder()
                .email(request.getEmail())
                .nickName(request.getNickName())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .build()).getId();
    }
}
