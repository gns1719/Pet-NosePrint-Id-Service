package com.example.pet_noseprint_id;

import com.example.pet_noseprint_id.user.domain.LocalUser;
import com.example.pet_noseprint_id.user.domain.User;
import com.example.pet_noseprint_id.user.dto.AddLocalUserRequestDTO;
import com.example.pet_noseprint_id.user.dto.LoginUserRequest;
import com.example.pet_noseprint_id.user.repository.LocalUserRepository;
import com.example.pet_noseprint_id.user.repository.UserRepository;
import com.example.pet_noseprint_id.user.service.LocalUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
public class UserControllerTest {

    @MockitoBean  // 최신 Spring Boot 테스트에서 사용하는 Mock 어노테이션
    private LocalUserService localUserService;

    @MockitoBean
    private LocalUserRepository localUserRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 필요한 경우 Mock 설정 초기화
    }

    @Test
    @DisplayName("로컬 유저 저장 테스트")
    void saveLocalUser() {
        // Given
        LocalUser localUser = new LocalUser(1L, "ididid", "pwpwpw");

        // When
        LocalUser saveUser = localUserRepository.save(localUser);

        // Then
        assertEquals(saveUser, localUser);
    }

    @Test
    @DisplayName("유저 저장 테스트")
    void saveUser() {
        // Given
        User user = new User(null, "local", "홍길동", "010-7630-1234", LocalDate.now(), "email@eee.com");

        // When
        User saveUser = userRepository.save(user);

        // Then
        assertEquals(saveUser, user);
    }

    @Test
    @DisplayName("로컬 유저 회원가입")
//    @Rollback
    @Transactional
    void registerLocalUserTest() throws Exception {
        // Given
        AddLocalUserRequestDTO request = new AddLocalUserRequestDTO(
                "홍길동", "010-1234-5678", "test@example.com", "testUser12", "password123"
        );


        // When & Then
        mockMvc.perform(post("/users/signup/local")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("signup-local-user",
                        requestFields(
                                fieldWithPath("name").description("사용자 이름"),
                                fieldWithPath("phoneNumber").description("전화번호"),
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("id").description("로그인 ID"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("status"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                ));
    }
}