package com.fitnessmedical.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;

import com.fitnessmedical.dto.member.MemberCreateRequest;
import com.fitnessmedical.dto.member.MemberResponse;
import com.fitnessmedical.service.FeedbackService;
import com.fitnessmedical.service.HealthRecordService;
import com.fitnessmedical.service.MemberAuthorizationService;
import com.fitnessmedical.service.MemberService;

/**
 * [공부/면접] MemberController 슬라이스 테스트
 *
 * Q. @MockitoBean(types = {...}) 클래스 레벨 선언은?
 * A. MemberController가 HealthRecordService, FeedbackService도 주입받으므로
 *    사용하지 않는 테스트라도 컨텍스트 로딩을 위해 mock 빈이 필요하다.
 *
 * Q. 400 테스트에서 service stub이 없는 이유는?
 * A. @Valid 실패 시 Controller 메서드 본문(Service 호출)까지 가지 않기 때문이다.
 */
@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockitoBean(types = {HealthRecordService.class, FeedbackService.class, MemberAuthorizationService.class})
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MemberService memberService;

    /**
     * [케이스] 잘못된 입력 → 400
     * - name 공백, age 0, progress 101 등 Bean Validation 위반
     * - 면접: "클라이언트가 잘못된 JSON을 보내면 Service 전에 거른다"
     */
    @Test
    @SuppressWarnings("null")
    @DisplayName("잘못된 회원 등록 요청은 400을 반환한다.")
    void createMember_invalidRequest_return400() throws Exception {
        String json = """
                {
                    "name" : "",
                    "gender" : "",
                    "age" : 0,
                    "height" : null,
                    "weight" : null,
                    "goal" : "",
                    "progress": 101
                }
                """;

        mockMvc.perform(post("/api/members")
                    .contentType(APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isBadRequest());
    }

    /**
     * [케이스] 정상 입력 → 201 + JSON 본문
     * - @ResponseStatus(CREATED) 또는 설정에 따라 201
     * - given(service.create(...)).willReturn(...): Controller는 Service 결과를 그대로 응답
     * - jsonPath("$.필드"): JSON Path로 응답 body를 검증
     * - 면접: Controller 테스트는 "변환/상태코드"에 집중하고, 복잡한 규칙은 ServiceTest로
     */
    @Test
    @SuppressWarnings("null")
    @WithMockUser(username = "pro01", roles = "PROFESSIONAL")
    @DisplayName("정상 회원 등록 요청은 201과 회원 정보를 반환한다.")
    void createMember_validRequest_returns201() throws Exception {
        MemberResponse response = new MemberResponse(
                6L,
                "홍길동",
                "남성",
                35,
                175.5,
                72.3,
                "건강 습관 만들기",
                10,
                "확인 필요",
                null,
                null,
                null
        );

        given(memberService.create(any(MemberCreateRequest.class)))
                .willReturn(response);

        String json = """
                {
                    "name" : "홍길동",
                    "gender" : "남성",
                    "age" : 35,
                    "height" : 175.5,
                    "weight" : 72.3,
                    "goal" : "건강 습관 만들기",
                    "progress": 10
                }
                """;

        mockMvc.perform(post("/api/members")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.status").value("확인 필요"));
    }

}
