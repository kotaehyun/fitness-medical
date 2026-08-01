package com.fitnessmedical.controller;

import com.fitnessmedical.service.FeedbackService;
import com.fitnessmedical.service.HealthRecordService;
import com.fitnessmedical.service.MemberService;
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

}

