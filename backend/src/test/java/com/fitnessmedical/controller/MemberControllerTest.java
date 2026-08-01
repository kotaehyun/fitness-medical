package com.fitnessmedical.controller;

import com.fitnessmedical.service.FeedbackService;
import com.fitnessmedical.service.HealthRecordService;
import com.fitnessmedical.service.MemberService;
import com.fitnessmedical.dto.member.MemberCreateRequest;
import com.fitnessmedical.dto.member.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    HealthRecordService healthRecordService;

    @MockitoBean
    FeedbackService feedbackService;

    @Test
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

    @Test
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

