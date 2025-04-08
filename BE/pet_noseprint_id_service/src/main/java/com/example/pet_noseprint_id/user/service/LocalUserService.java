package com.example.pet_noseprint_id.user.service;

import com.example.pet_noseprint_id.user.config.jwt.JwtProvider;
import com.example.pet_noseprint_id.user.domain.LocalUser;
import com.example.pet_noseprint_id.user.domain.User;
import com.example.pet_noseprint_id.user.dto.LoginUserRequest;
import com.example.pet_noseprint_id.user.dto.LoginUserResponse;
import com.example.pet_noseprint_id.user.repository.LocalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocalUserService {
    private final LocalUserRepository localUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;

    private Long accessExpireTimeMs = 60 * 60 * 250L;  // 15분
    private Long refreshExpireTimeMs = 14 * 24 * 60 * 60 * 1000L;  // 14일

    // 이메일 중복 검사
    public void isIdDuplicate(String id) {
        if (localUserRepository.existsById(id)) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
    }

    //회원가입
    public Long saveLocal(LocalUser localUser) {

        // 로컬 회원 정보 저장 (User_ID 참조)
        return localUserRepository.save(localUser).getUserKey();
    }

    // 로그인
    public LoginUserResponse login(LoginUserRequest request) {

        // id 확인
        LocalUser localUser = localUserRepository.findById(request.getId())
                .orElseThrow(()-> new IllegalArgumentException("ID 존재하지 않음"));

        // password 틀림
        if (!passwordEncoder.matches(request.getPassword(), localUser.getPassword())) {
            throw new IllegalArgumentException("password 틀림");
        }

        String accessToken = jwtProvider.createAccessToken(localUser.getUserKey(), accessExpireTimeMs);
        String refreshToken = jwtProvider.createRefreshToken(localUser.getUserKey(), refreshExpireTimeMs);

        refreshTokenService.saveTokenInfo(localUser.getUserKey(), refreshToken);

        return LoginUserResponse.builder()
                .userId(localUser.getUserKey())
                .id(localUser.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();

    }

}