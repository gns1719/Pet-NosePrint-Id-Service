package com.example.pet_noseprint_id.user.service;


import com.example.pet_noseprint_id.user.config.jwt.JwtProvider;
import com.example.pet_noseprint_id.user.domain.OAuthUser;
import com.example.pet_noseprint_id.user.domain.User;
import com.example.pet_noseprint_id.user.dto.LoginUserResDTO;
import com.example.pet_noseprint_id.user.dto.oauth.GoogleUserInfoResDTO;
import com.example.pet_noseprint_id.user.dto.oauth.KakaoUserInfoResDTO;
import com.example.pet_noseprint_id.user.dto.oauth.NaverUserInfoResDTO;
import com.example.pet_noseprint_id.user.repository.OAuthUserRepository;
import com.example.pet_noseprint_id.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final NaverService naverService;

    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    private final OAuthUserRepository oAuthUserRepository;
    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private Long accessExpireTimeMs = 60 * 60 * 250L;  // 15분
    private Long refreshExpireTimeMs = 14 * 24 * 60 * 60 * 1000L;  // 14일

    @Transactional
    public LoginUserResDTO handleKakao(String code) {
        // 1. 액세스 토큰 받아오기
        String accessToken = kakaoService.getAccessTokenFromKakao(code);

        // 2. 사용자 정보 조회
        KakaoUserInfoResDTO userInfo = kakaoService.getUserInfo(accessToken);
        String kakaoId = String.valueOf(userInfo.getId());
        String email = userInfo.getKakaoAccount().getEmail();
        String nickname = userInfo.getKakaoAccount().getProfile().getNickName();

        // 3. OAuth 유저 존재 여부 확인
        Optional<OAuthUser> oAuthUserOpt = oAuthUserRepository.findByProviderAndProviderKey("kakao", kakaoId);

        Long userKey;

        if (oAuthUserOpt.isEmpty()) {
            // 3-1. 회원가입 로직
            User user = new User(null, "oauth", nickname, "010-7894-456", LocalDate.now(), email);
            userKey = userRepository.save(user).getUserKey();

            OAuthUser oAuthUser = new OAuthUser(null, userKey, "kakao", kakaoId);
            oAuthUserRepository.save(oAuthUser);
        } else {
            // 3-2. 로그인 (이미 회원)
            userKey = oAuthUserOpt.get().getUserKey();
        }

        String serverAccessToken  = jwtProvider.createAccessToken(userKey, accessExpireTimeMs);
        String serverRefreshToken  = jwtProvider.createRefreshToken(userKey, refreshExpireTimeMs);

        refreshTokenService.saveTokenInfo(userKey, serverRefreshToken);

        // 클라이언트에 토큰 응답은 Controller에서 처리
        return LoginUserResDTO.builder()
                .userId(userKey)
                .accessToken(serverAccessToken)
                .refreshToken(serverRefreshToken)
                .build();
    }

    public LoginUserResDTO handleGoogle(String code) {
        String accessToken = googleService.getAccessTokenFromGoogle(code);
        GoogleUserInfoResDTO userInfo = googleService.getUserInfo(accessToken);

        // providerKey: Google의 유저 ID
        Optional<OAuthUser> existingUser = oAuthUserRepository.findByProviderAndProviderKey("google", userInfo.getId());

        Long userKey;
        if (existingUser.isPresent()) {
            userKey = existingUser.get().getUserKey();
        } else {
            User newUser = new User(null, "oauth", userInfo.getName(), "", LocalDate.now(), userInfo.getEmail());
            userKey = userService.saveUser(newUser);
            OAuthUser oauthUser = new OAuthUser(null, userKey, "google", userInfo.getId());
            oAuthUserRepository.save(oauthUser);
        }

        String serverAccessToken = jwtProvider.createAccessToken(userKey, accessExpireTimeMs);
        String serverRefreshToken = jwtProvider.createRefreshToken(userKey, refreshExpireTimeMs);
        refreshTokenService.saveTokenInfo(userKey, serverRefreshToken);

        return LoginUserResDTO.builder()
                .userId(userKey)
                .accessToken(serverAccessToken)
                .refreshToken(serverRefreshToken)
                .build();
    }

    public LoginUserResDTO handleNaver(String code, String state) {
        // 1. 액세스 토큰 받아오기
        String accessToken = naverService.getAccessTokenFromNaver(code, state);

        // 2. 사용자 정보 조회
        NaverUserInfoResDTO userInfo = naverService.getUserInfo(accessToken);
        String naverId = String.valueOf(userInfo.getResponse().getId());
        String email = userInfo.getResponse().getEmail();
        String nickname = userInfo.getResponse().getName();
        String phoneNumber = userInfo.getResponse().getMobile();

        // 3. OAuth 유저 존재 여부 확인
        Optional<OAuthUser> oAuthUserOpt = oAuthUserRepository.findByProviderAndProviderKey("naver", naverId);

        Long userKey;

        if (oAuthUserOpt.isEmpty()) {
            // 3-1. 회원가입 로직
            User user = new User(null, "oauth", nickname, phoneNumber, LocalDate.now(), email);
            userKey = userRepository.save(user).getUserKey();

            OAuthUser oAuthUser = new OAuthUser(null, userKey, "naver", naverId);
            oAuthUserRepository.save(oAuthUser);
        } else {
            // 3-2. 로그인 (이미 회원)
            userKey = oAuthUserOpt.get().getUserKey();
        }

        String serverAccessToken  = jwtProvider.createAccessToken(userKey, accessExpireTimeMs);
        String serverRefreshToken  = jwtProvider.createRefreshToken(userKey, refreshExpireTimeMs);

        refreshTokenService.saveTokenInfo(userKey, serverRefreshToken);

        // 클라이언트에 토큰 응답은 Controller에서 처리
        return LoginUserResDTO.builder()
                .userId(userKey)
                .accessToken(serverAccessToken)
                .refreshToken(serverRefreshToken)
                .build();
    }
}
