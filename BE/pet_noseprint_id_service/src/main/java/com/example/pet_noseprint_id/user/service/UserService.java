package com.example.pet_noseprint_id.user.service;


import com.example.pet_noseprint_id.user.domain.LocalUser;
import com.example.pet_noseprint_id.user.domain.User;
import com.example.pet_noseprint_id.user.dto.UserInfoResDTO;
import com.example.pet_noseprint_id.user.repository.LocalUserRepository;
import com.example.pet_noseprint_id.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final LocalUserRepository localUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


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

    // 비밀번호 변경
    public void changePassword(Long userKey, String currentPassword, String newPassword) {
        LocalUser localUser = localUserRepository.findByUserKey(userKey)
                .orElseThrow(() -> new IllegalArgumentException("로컬 로그인 사용자가 아닙니다."));

        if (!passwordEncoder.matches(currentPassword, localUser.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        String encoded = passwordEncoder.encode(newPassword);
        localUserRepository.updatePassword(userKey, encoded);
    }

    //비밀번호 찾기 - 이메일로 임시 비밀번호 전송
    public void findPassword(String userId) {
        LocalUser localUser = localUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Long userKey = localUser.getUserKey();
        User user = userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보가 존재하지 않습니다."));

        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("이메일 정보가 없습니다.");
        }

        String tempPassword = generateTempPassword();
        String encoded = passwordEncoder.encode(tempPassword);

        localUserRepository.updatePassword(userKey, encoded);
        emailService.sendStyledTempPassword(email, tempPassword);
    }

    //임시 비밀번호 생성
    private String generateTempPassword() {
        int length = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    //유저 정보 변경
    public void updateUserInfo(Long userKey, String name, String phoneNumber, String email) {
        User user = userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);

        userRepository.save(user);
    }

    //유저 정보 조회
    public UserInfoResDTO getUserInfo(Long userKey) {
        return userRepository.findUserInfoByUserKey(userKey)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
