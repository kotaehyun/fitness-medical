package com.fitnessmedical.service;

import com.fitnessmedical.dto.feedback.FeedbackRequest;
import com.fitnessmedical.entity.Feedback;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.dto.feedback.FeedbackResponse;
import com.fitnessmedical.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

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

    public FeedbackResponse create(Long memberId, FeedbackRequest request) {

        Member member = memberService.getMember(memberId);

        Feedback feedback = new Feedback(
                member,
                request.author(),
                request.role(),
                LocalDate.now(),
                request.content()
        );

        Feedback saved = feedbackRepository.save(feedback);

        return FeedbackResponse.from(saved);
    }
}
