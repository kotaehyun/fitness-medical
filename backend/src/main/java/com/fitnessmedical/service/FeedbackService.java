package com.fitnessmedical.service;

import com.fitnessmedical.dto.feedback.FeedbackResponse;
import com.fitnessmedical.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** 전문가 피드백 조회 로직을 담당하는 Service입니다. */
@Service
@Transactional(readOnly = true)
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final MemberService memberService;

    public FeedbackService(FeedbackRepository feedbackRepository, MemberService memberService) {
        this.feedbackRepository = feedbackRepository;
        this.memberService = memberService;
    }

    public List<FeedbackResponse> findByMemberId(Long memberId) {
        // 잘못된 회원 ID로 빈 배열만 반환하지 않도록 회원 존재 여부를 확인합니다.
        memberService.getMember(memberId);
        return feedbackRepository.findByMemberIdOrderByWrittenDateDesc(memberId).stream()
                .map(FeedbackResponse::from)
                .toList();
    }
}
