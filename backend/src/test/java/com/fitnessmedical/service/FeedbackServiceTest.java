package com.fitnessmedical.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fitnessmedical.dto.feedback.FeedbackRequest;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.entity.Feedback;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.repository.FeedbackRepository;

class FeedbackServiceTest {

    final FeedbackRepository feedbackRepository = mock(FeedbackRepository.class);
    final MemberService memberService = mock(MemberService.class);
    final FeedbackService feedbackService = new FeedbackService(feedbackRepository, memberService);

    @Test
    @DisplayName("피드백 작성자와 역할은 요청이 아니라 세션 Account에서 채운다.")
    void create_usesSessionAccountForAuthor() {
        Member member = mock(Member.class);
        Account writer = new Account(
                "pro01",
                "encoded",
                "김전문가",
                AccountRole.PROFESSIONAL,
                null
        );
        given(memberService.getMember(1L)).willReturn(member);
        given(feedbackRepository.save(any(Feedback.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        feedbackService.create(
                1L,
                new FeedbackRequest("규칙적인 걷기 시간을 유지해 보세요."),
                writer
        );

        ArgumentCaptor<Feedback> captor = ArgumentCaptor.forClass(Feedback.class);
        verify(feedbackRepository).save(captor.capture());
        Feedback saved = captor.getValue();
        assertThat(saved.getAuthor()).isEqualTo("김전문가");
        assertThat(saved.getRole()).isEqualTo("전문가");
        assertThat(saved.getContent()).isEqualTo("규칙적인 걷기 시간을 유지해 보세요.");
    }
}
