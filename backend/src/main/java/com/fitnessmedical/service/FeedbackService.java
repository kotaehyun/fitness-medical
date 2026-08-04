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

    // 클래스 전체엔 readOnly=true가 적용되어 있으니, 저장하는 이 메서드는 개별 @Transactional이 필요합니다.
    @Transactional
    public FeedbackResponse create(Long memberId, FeedbackRequest request ) {

        // 1.회원이 존재하는지 확인하면 Member Entity를 가져옵니다. (HealthRecordService.create()와 동일 패턴)
        Member member = memberService.getMember(memberId);

        // 2. DTO 값이 3개 + member + 서버가 만드는 writtenDate()
        //    Feedback 생성자 순서: member, author, role, writtenDate, content
        Feedback feedback = new Feedback(
                member, request.author(), request.role(), LocalDate.now(), request.content()
        );

        // 3. save()로 저장하고, 응답 DTO로 변환해서 반환합니다.
        return FeedbackResponse.from(feedbackRepository.save(feedback));
    }
}
