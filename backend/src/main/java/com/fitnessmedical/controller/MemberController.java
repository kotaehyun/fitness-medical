package com.fitnessmedical.controller;

import com.fitnessmedical.dto.feedback.FeedbackResponse;
import com.fitnessmedical.dto.feedback.FeedbackRequest;
import com.fitnessmedical.dto.health.HealthRecordRequest;
import com.fitnessmedical.dto.health.HealthRecordResponse;
import com.fitnessmedical.dto.member.MemberCreateRequest;
import com.fitnessmedical.dto.member.MemberResponse;
import com.fitnessmedical.dto.member.MemberUpdateRequest;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.service.FeedbackService;
import com.fitnessmedical.service.HealthRecordService;
import com.fitnessmedical.service.MemberAuthorizationService;
import com.fitnessmedical.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * [공부/면접] 회원 및 하위 리소스(건강 기록, 피드백) HTTP API Controller입니다.
 *
 * Q. Controller → Service → Repository 계층에서 Controller 역할은?
 * A. URL 매핑(@GetMapping, @PathVariable), 요청 본문 역직렬화(@RequestBody),
 *    Bean Validation(@Valid), HTTP 상태 코드(@ResponseStatus)만 담당합니다.
 *    비즈니스 규칙·DB 접근은 Service/Repository에 위임합니다.
 *
 * Q. @PathVariable Long memberId는?
 * A. /api/members/{memberId} 경로의 {memberId}를 Long으로 변환해 Service에 전달합니다.
 *
 * Q. 왜 @AuthenticationPrincipal 을 쓰나?
 * A. 세션의 loginId로 소유권·역할을 검사한다. URL memberId만 믿으면 타인 데이터에 접근한다.
 *
 * Q. Service에서 발생하는 예외와 HTTP 코드는?
 * A. ResourceNotFoundException → 404, InvalidRequestException → 400,
 *    DuplicateResourceException → 409, ForbiddenException → 403
 *    (GlobalExceptionHandler가 처리)
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final HealthRecordService healthRecordService;
    private final FeedbackService feedbackService;
    private final MemberAuthorizationService memberAuthorizationService;

    public MemberController(MemberService memberService, HealthRecordService healthRecordService,
                            FeedbackService feedbackService,
                            MemberAuthorizationService memberAuthorizationService) {
        this.memberService = memberService;
        this.healthRecordService = healthRecordService;
        this.feedbackService = feedbackService;
        this.memberAuthorizationService = memberAuthorizationService;
    }

    /** [공부/면접] GET /api/members — 회원 전체 목록 (전문가) */
    @GetMapping
    public List<MemberResponse> getMembers(@AuthenticationPrincipal UserDetails user) {
        memberAuthorizationService.requireProfessional(loginId(user));
        return memberService.findAll();
    }

    /**
     * [공부/면접] GET /api/members/{memberId} — 회원 단건 조회
     * @PathVariable: URL 경로 변수를 메서드 파라미터에 바인딩
     */
    @GetMapping("/{memberId}")
    public MemberResponse getMember(
            @PathVariable Long memberId,
            @AuthenticationPrincipal UserDetails user
    ) {
        memberAuthorizationService.assertCanAccessMember(loginId(user), memberId);
        return memberService.findById(memberId);
    }

    /** [공부/면접] GET /api/members/{memberId}/records — 건강 기록 목록 */
    @GetMapping("/{memberId}/records")
    public List<HealthRecordResponse> getRecords(
            @PathVariable Long memberId,
            @AuthenticationPrincipal UserDetails user
    ) {
        memberAuthorizationService.assertCanAccessMember(loginId(user), memberId);
        return healthRecordService.findByMemberId(memberId);
    }

    /**
     * [공부/면접] POST /api/members/{memberId}/records — 건강 기록 등록
     *
     * @Valid @RequestBody: JSON 본문을 DTO로 변환 후 @Min/@Max 등 검증(실패 시 400)
     * @ResponseStatus(CREATED): 성공 시 201 반환
     */
    @PostMapping("/{memberId}/records")
    @ResponseStatus(HttpStatus.CREATED)
    public HealthRecordResponse createRecord(
            @PathVariable Long memberId,
            @Valid @RequestBody HealthRecordRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        memberAuthorizationService.assertCanAccessMember(loginId(user), memberId);
        return healthRecordService.create(memberId, request);
    }

    /** [공부/면접] GET /api/members/{memberId}/feedback — 피드백 목록 */
    @GetMapping("/{memberId}/feedback")
    public List<FeedbackResponse> getFeedback(
            @PathVariable Long memberId,
            @AuthenticationPrincipal UserDetails user
    ) {
        memberAuthorizationService.assertCanAccessMember(loginId(user), memberId);
        return feedbackService.findByMemberId(memberId);
    }

    /**
     * [공부/면접] POST /api/members/{memberId}/feedback — 피드백 등록
     * author·role은 요청 body가 아니라 세션 Account에서 채운다.
     */
    @PostMapping("/{memberId}/feedback")
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackResponse createFeedback(
            @PathVariable Long memberId,
            @Valid @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        Account writer = memberAuthorizationService.requireProfessional(loginId(user));
        return feedbackService.create(memberId, request, writer);
    }

    /**
     * [공부/면접] POST /api/members — 회원 등록
     * @Valid: MemberCreateRequest 필드 검증 후 memberService.create() 위임
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse createMember(
            @Valid @RequestBody MemberCreateRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        memberAuthorizationService.requireProfessional(loginId(user));
        return memberService.create(request);
    }

    /**
     * [공부/면접] PUT /api/members/{memberId} — 회원 정보 수정
     * PUT은 기존 리소스 전체/부분 갱신에 사용, memberId로 대상 식별
     */
    @PutMapping("/{memberId}")
    public MemberResponse updateMember(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberUpdateRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        memberAuthorizationService.requireProfessional(loginId(user));
        return memberService.update(memberId, request);
    }

    /**
     * [공부/면접] DELETE /api/members/{memberId} — 회원 삭제
     * void 반환 + @ResponseStatus(NO_CONTENT) → 성공 시 204, 본문 없음
     */
    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(
            @PathVariable Long memberId,
            @AuthenticationPrincipal UserDetails user
    ) {
        memberAuthorizationService.requireProfessional(loginId(user));
        memberService.delete(memberId);
    }

    private String loginId(UserDetails user) {
        return user == null ? null : user.getUsername();
    }

}
