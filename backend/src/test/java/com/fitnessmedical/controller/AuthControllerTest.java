package com.fitnessmedical.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fitnessmedical.service.AccountService;
import com.fitnessmedical.common.DuplicateResourceException;
import com.fitnessmedical.common.InvalidRequestException;
import com.fitnessmedical.common.ResourceNotFoundException;
import com.fitnessmedical.dto.account.AccountCreateRequest;

/**
 * [공부/면접] AuthController 슬라이스 테스트 (@WebMvcTest)
 *
 * Q. @WebMvcTest vs @SpringBootTest 차이는?
 * A. WebMvcTest는 웹 계층(Controller, @ControllerAdvice 등)만 띄운다.
 *    전체 컨텍스트/DB를 올리지 않아 빠르다. (= 슬라이스 테스트)
 *
 * Q. addFilters = false 는?
 * A. Spring Security 필터 체인을 끈다.
 *    이 테스트 목적은 "인증"이 아니라 "요청 검증·응답 상태"이므로 필터를 제외한다.
 *
 * Q. @MockitoBean 은?
 * A. Spring 컨텍스트에 mock 빈을 등록한다.
 *    Controller가 필요로 하는 의존성을 가짜로 채워 생성/주입이 되게 한다.
 *
 * Q. Service 단위 테스트와 역할 분담은?
 * A. ServiceTest: 예외 타입/비즈니스 규칙
 *    ControllerTest: HTTP 상태코드·JSON 응답 (검증 실패, Handler 매핑 등)
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    // MockMvc: 실제 서버 없이 Controller로 HTTP 요청을 흉내 낸다.
    @Autowired
    MockMvc mockMvc;

    /**
     * signup → accountService.create() 호출.
     * @Valid 실패 테스트에서는 Controller 앞에서 막히므로 이 mock은 호출되지 않는다.
     * (면접: "검증 실패 시 Service가 호출되지 않는다"를 설명할 수 있으면 좋다)
     */
    @MockitoBean
    AccountService accountService;

    /**
     * AuthController 생성자 파라미터라서 등록만 한다.
     * 이 파일의 signup 검증 테스트에서는 AuthenticationManager를 쓰지 않는다.
     */
    @MockitoBean
    AuthenticationManager authenticationManager;

    /**
     * [케이스] memberId = 0 → 400
     *
     * 흐름:
     * 1) JSON → AccountCreateRequest 바인딩
     * 2) @Valid → @Positive 위반 (0은 양수가 아님)
     * 3) MethodArgumentNotValidException 발생
     * 4) GlobalExceptionHandler → HTTP 400 + message
     *
     * 면접 포인트:
     * - Bean Validation(@Positive)은 "입력 형식/범위"
     * - Service의 InvalidRequestException은 "업무 규칙"
     * - 둘 다 400일 수 있지만 책임이 다르다
     */
    @Test
    @SuppressWarnings("null")
    @DisplayName("memberId가 0이면 회원가입은 400을 반환한다.")
    void signup_memberIdZero_returns400() throws Exception {
        String json = """
                {
                    "loginId": "member01",
                    "password": "password123",
                    "displayName": "회원",
                    "role": "MEMBER",
                    "memberId": 0
                }
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                // HTTP 상태
                .andExpect(status().isBadRequest())
                // 응답 body의 커스텀 에러 형식(ApiErrorResponse)
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("회원 ID는 양수여야 합니다."));
    }

    /**
     * [케이스] memberId = -1 → 400
     * @Positive는 음수도 거부한다. (0과 같은 Bean Validation 경로)
     */
    @Test
    @SuppressWarnings("null")
    @DisplayName("memberId가 음수이면 회원가입은 400을 반환한다.")
    void signup_memberIdNegative_returns400() throws Exception {
        String json = """
                {
                    "loginId": "member01",
                    "password": "password123",
                    "displayName": "회원",
                    "role": "MEMBER",
                    "memberId": -1
                }
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("회원 ID는 양수여야 합니다."));
    }

    /**
     * [케이스] MEMBER + memberId 없음 → 400
     *
     * Bean Validation이 아니라 Service 업무 규칙이다.
     * mock이 InvalidRequestException을 던지면 GlobalExceptionHandler가 400으로 매핑한다.
     */
    @Test
    @SuppressWarnings("null")
    @DisplayName("MEMBER 계정에 memberId가 없으면 회원가입은 400을 반환한다.")
    void signup_memberWithoutMemberId_returns400() throws Exception {
        given(accountService.create(any(AccountCreateRequest.class)))
                .willThrow(new InvalidRequestException("MEMBER 계정은 회원 연결이 필요합니다."));

        String json = """
                {
                    "loginId": "member01",
                    "password": "password123",
                    "displayName": "회원",
                    "role": "MEMBER"
                }
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("MEMBER 계정은 회원 연결이 필요합니다."));
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("존재하지 않는 memberId면 회원가입은 404를 반환한다.")
    void signup_unknownMemberId_returns404() throws Exception {
        given(accountService.create(any(AccountCreateRequest.class)))
                .willThrow(new ResourceNotFoundException("연결할 회원을 찾을 수 없습니다."));

        String json = """
                {
                    "loginId": "member01",
                    "password": "password123",
                    "displayName": "회원",
                    "role": "MEMBER",
                    "memberId": 999
                }
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("연결할 회원을 찾을 수 없습니다."));
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("이미 연결된 memberId면 회원가입은 409를 반환한다.")
    void signup_alreadyLinkedMemberId_returns409() throws Exception {
        given(accountService.create(any(AccountCreateRequest.class)))
                .willThrow(new DuplicateResourceException("이미 연결된 회원입니다."));

        String json = """
                {
                    "loginId": "member01",
                    "password": "password123",
                    "displayName": "회원",
                    "role": "MEMBER",
                    "memberId": 1
                }
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("이미 연결된 회원입니다."));
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("UNIQUE 제약 레이스면 회원가입은 409를 반환한다.")
    void signup_dataIntegrityViolation_returns409() throws Exception {
        given(accountService.create(any(AccountCreateRequest.class)))
                .willThrow(new DataIntegrityViolationException("uk_accounts_login_id"));

        String json = """
                {
                    "loginId": "member01",
                    "password": "password123",
                    "displayName": "회원",
                    "role": "MEMBER",
                    "memberId": 1
                }
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("요청이 이미 처리되었거나 중복된 값입니다."));
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("로그인 아이디가 비어 있으면 400을 반환한다.")
    void login_blankLoginId_returns400() throws Exception {
        String json = """
                {
                    "loginId": "",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("로그인 아이디는 필수입니다."));
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("PROFESSIONAL 계정에 memberId가 있으면 회원가입은 400을 반환한다.")
    void signup_professionalWithMemberId_returns400() throws Exception {
        given(accountService.create(any(AccountCreateRequest.class)))
                .willThrow(new InvalidRequestException(
                        "PROFESSIONAL 계정은 memberId를 가질 수 없습니다."
                ));

        String json = """
                {
                    "loginId": "pro01",
                    "password": "password123",
                    "displayName": "전문가",
                    "role": "PROFESSIONAL",
                    "memberId": 1
                }
                """;

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("PROFESSIONAL 계정은 memberId를 가질 수 없습니다."));
    }
}
