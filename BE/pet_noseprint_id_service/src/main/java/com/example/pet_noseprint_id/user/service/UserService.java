package com.example.pet_noseprint_id.user.service;


import com.example.pet_noseprint_id.user.domain.User;
import com.example.pet_noseprint_id.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 이메일 중복 검사
    public void checkEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
    }

    // User 저장 (공통 로직)
    @Transactional
    public Long saveUser(User user) {
        checkEmailDuplicate(user.getEmail()); // 저장 전에 중복 검사
        return userRepository.save(user).getUserKey();
    }


}
