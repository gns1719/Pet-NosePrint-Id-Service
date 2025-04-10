package com.example.pet_noseprint_id;

import com.example.pet_noseprint_id.user.domain.LocalUser;
import com.example.pet_noseprint_id.user.domain.User;
import com.example.pet_noseprint_id.user.dto.CreateAccessTokenReqDTO;
import com.example.pet_noseprint_id.user.dto.LoginUserReqDTO;
import com.example.pet_noseprint_id.user.repository.LocalUserRepository;
import com.example.pet_noseprint_id.user.repository.UserRepository;
import com.example.pet_noseprint_id.user.service.LocalUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class UserControllerTest {

    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocalUserService localUserService;

    @BeforeEach
    void setUpMockMvcForRestDocs(WebApplicationContext webApplicationContext,
                                 RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("로컬 저장")
    @Transactional
    void saveLocalUser() {
        // Given
        LocalUser localUser = new LocalUser(null,1L, "ididid", "pwpwpw");

        // When
        LocalUser saveUser = localUserRepository.save(localUser);

        // Then
        assertEquals(saveUser, localUser);
    }

    @Test
    @DisplayName("유저 저장")
    @Transactional
    void saveUser() {
        // Given
        User user = new User(null, "local", "홍길동", "010-7630-1234", LocalDate.now(), "email@eee.com");

        // When
        User saveUser = userRepository.save(user);

        // Then
        assertEquals(user.getName(), saveUser.getName());
        assertEquals(user.getEmail(), saveUser.getEmail());
        assertEquals(user.getLoginType(), saveUser.getLoginType());
        assertEquals(user.getPhoneNumber(), saveUser.getPhoneNumber());
    }

    @Test
    @DisplayName("로컬 유저 회원가입")
    @Transactional
    void registerLocalUserTest() throws Exception {
        // Given
        CreateAccessTokenReqDTO.AddLocalUserReqDTO request = new CreateAccessTokenReqDTO.AddLocalUserReqDTO(
                "홍길동", "010-1234-5678", "test@example.com", "testUser12", "password123"
        );


        // When & Then
        mockMvc.perform(post("/users/signup/local")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("signup-local-user",
                        requestFields(
                                fieldWithPath("name").description("사용자 이름").type(JsonFieldType.STRING),
                                fieldWithPath("phoneNumber").description("전화번호").type(JsonFieldType.STRING),
                                fieldWithPath("email").description("사용자 이메일").type(JsonFieldType.STRING),
                                fieldWithPath("id").description("로그인 ID").type(JsonFieldType.STRING),
                                fieldWithPath("password").description("비밀번호").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("status").description("status").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    @Test
    @DisplayName("로컬 유저 로그인")
    @Transactional
    public void localLoginTest() throws Exception {
        // Given
        LoginUserReqDTO request = new LoginUserReqDTO("gns1719", "1254");


        // When & Then
        mockMvc.perform(post("/users/login/local")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("login-local-user",
                        requestFields(
                                fieldWithPath("id").description("로그인 ID"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("status"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.userId").description("사용자 고유 ID"),
                                fieldWithPath("data.id").description("로그인 ID"),
                                fieldWithPath("data.accessToken").description("Access Token"),
                                fieldWithPath("data.refreshToken").description("Refresh Token"),
                                fieldWithPath("data.tokenType").description("토큰 타입 (예: Bearer)")
                        )
                ));
    }
}