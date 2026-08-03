package com.fitnessmedical.controller;

import com.fitnessmedical.dto.feedback.FeedbackResponse;
import com.fitnessmedical.dto.feedback.FeedbackRequest;
import com.fitnessmedical.dto.health.HealthRecordRequest;
import com.fitnessmedical.dto.health.HealthRecordResponse;
import com.fitnessmedical.dto.member.MemberCreateRequest;
import com.fitnessmedical.dto.member.MemberResponse;
import com.fitnessmedical.dto.member.MemberUpdateRequest;
import com.fitnessmedical.service.FeedbackService;
import com.fitnessmedical.service.HealthRecordService;
import com.fitnessmedical.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 회원 관련 HTTP 요청을 받는 Controller입니다.
 * Controller는 요청값 확인과 Service 호출, 응답 반환에 집중합니다.
 */
@RestController
// 이 Controller의 모든 API 앞에는 /api/members가 붙습니다.
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final HealthRecordService healthRecordService;
    private final FeedbackService feedbackService;

    public MemberController(MemberService memberService, HealthRecordService healthRecordService,
                            FeedbackService feedbackService) {
        this.memberService = memberService;
        this.healthRecordService = healthRecordService;
        this.feedbackService = feedbackService;
    }

    // GET /api/members 요청을 처리합니다.
    @GetMapping
    public List<MemberResponse> getMembers() {
        return memberService.findAll();
    }

    // {memberId}는 URL 경로의 값을 의미합니다.
    // @PathVariable이 해당 값을 Long 타입 매개변수로 변환합니다.
    @GetMapping("/{memberId}")
    public MemberResponse getMember(@PathVariable Long memberId) {
        return memberService.findById(memberId);
    }

    @GetMapping("/{memberId}/records")
    public List<HealthRecordResponse> getRecords(@PathVariable Long memberId) {
        return healthRecordService.findByMemberId(memberId);
    }

    // POST 요청의 JSON 본문은 @RequestBody를 통해 HealthRecordRequest로 변환됩니다.
    @PostMapping("/{memberId}/records")
    // 등록 성공 상태 코드인 201 Created를 반환합니다.
    @ResponseStatus(HttpStatus.CREATED)
    public HealthRecordResponse createRecord(@PathVariable Long memberId,
                                             // @Valid가 DTO의 @Min, @Max 같은 검증 조건을 실행합니다.
                                             @Valid @RequestBody HealthRecordRequest request) {
        return healthRecordService.create(memberId, request);
    }

    @GetMapping("/{memberId}/feedback")
    public List<FeedbackResponse> getFeedback(@PathVariable Long memberId) {
        return feedbackService.findByMemberId(memberId);
    }

    @PostMapping("/{memberId}/feedback")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResponse createFeedback(
            @PathVariable Long memberId,
            @Valid @RequestBody FeedbackRequest request
    ) {
        return feedbackService.create(memberId, request);
    }

    // POST /api/members 요청을 처리합니다. (클래스 레벨 경로 그대로 사용, 추가 경로 없음)
    @PostMapping
    // 등록 성공 상태 코드 201을 반환합니다. (createRecord()랑 동일한 패턴)
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse createMember(@Valid @RequestBody MemberCreateRequest request){
        return memberService.create(request);
    }

    // PUT /api/members/{memberId} 요청을 처리합니다.
    // 이미 있는 회원을 "고치는" 거라 POST가 아니라 PUT을 쓰고,
    // 어떤 회원인지 알아야 하니 {memberId}가 URL에 들어갑니다.
    @PutMapping("/{memberId}")
    public MemberResponse updateMember(@PathVariable Long memberId,
                                       @Valid @RequestBody MemberUpdateRequest request) {
        return memberService.update(memberId, request);
    }

    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long memberId){
        memberService.delete(memberId);
    }

}
