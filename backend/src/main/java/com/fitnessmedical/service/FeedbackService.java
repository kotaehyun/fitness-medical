package com.fitnessmedical.service;

import com.fitnessmedical.dto.feedback.FeedbackRequest;
import com.fitnessmedical.dto.feedback.FeedbackResponse;
import com.fitnessmedical.repository.FeedbackRepository;
import com.fitnessmedical.entity.Feedback;
import com.fitnessmedical.entity.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDate;

/**
 * [공부/면접] 회원별 전문가 피드백 조회·등록을 담당하는 Service 계층입니다.
 *
 * Q. HealthRecordService와 구조가 비슷한 이유는?
 * A. "memberId로 부모(Member) 존재 확인 → 자식 Entity CRUD" 패턴을 재사용합니다.
 *    Service 간 협력(memberService.getMember)으로 404 규칙을 한곳에 모읍니다.
 *
 * Q. writtenDate를 서버에서 LocalDate.now()로 넣는 이유는?
 * A. 클라이언트가 임의 날짜를 보내는 것을 막고, 등록 시점을 서버가 신뢰할 수 있게 합니다.
 *
 * 예외: ResourceNotFoundException(404) — getMember()에서 발생
 */
@Service
@Transactional(readOnly = true)
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final MemberService memberService;

    public FeedbackService(FeedbackRepository feedbackRepository, MemberService memberService) {
        this.feedbackRepository = feedbackRepository;
        this.memberService = memberService;
    }

    /**
     * [공부/면접] 특정 회원에 대한 피드백 목록을 최신 작성일 순으로 조회합니다.
     *
     * 흐름: getMember()로 회원 존재 확인 → feedbackRepository 조회 → DTO 변환
     */
    public List<FeedbackResponse> findByMemberId(Long memberId) {
        memberService.getMember(memberId);
        return feedbackRepository.findByMemberIdOrderByWrittenDateDesc(memberId).stream()
                .map(FeedbackResponse::from)
                .toList();
    }

    /**
     * [공부/면접] 새 피드백을 등록합니다.
     *
     * 흐름: getMember() → FeedbackRequest + LocalDate.now()로 Entity 생성
     *       → save(INSERT) → FeedbackResponse 반환
     *
     * 면접 포인트: @Transactional(쓰기) — 조회·INSERT가 하나의 트랜잭션으로 처리됩니다.
     */
    @Transactional
    public FeedbackResponse create(Long memberId, FeedbackRequest request ) {

        Member member = memberService.getMember(memberId);

        Feedback feedback = new Feedback(
                member, request.author(), request.role(), LocalDate.now(), request.content()
        );

        return FeedbackResponse.from(feedbackRepository.save(feedback));
    }
}
