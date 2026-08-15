package com.fitnessmedical.service;

import com.fitnessmedical.common.InvalidRequestException;
import com.fitnessmedical.common.ResourceNotFoundException;
import com.fitnessmedical.dto.member.MemberCreateRequest;
import com.fitnessmedical.dto.member.MemberResponse;
import com.fitnessmedical.dto.member.MemberUpdateRequest;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.entity.MemberStatus;
import com.fitnessmedical.repository.AccountRepository;
import com.fitnessmedical.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * [공부/면접] 회원(Member) CRUD 비즈니스 로직을 담당하는 Service 계층입니다.
 *
 * Q. Controller → Service → Repository 계층을 나누는 이유는?
 * A. Controller는 HTTP 요청/응답(@PathVariable, @Valid)만, Repository는 JPA 쿼리만,
 *    Service는 "회원이 없으면 404" 같은 도메인 규칙을 처리해 각 계층 책임을 분리합니다.
 *
 * Q. @Transactional(readOnly = true)를 클래스에 붙이면?
 * A. findAll, findById 등 조회는 읽기 전용 트랜잭션으로 실행되고,
 *    create/update/delete처럼 DB를 변경하는 메서드는 메서드 단위 @Transactional로 덮어씁니다.
 *
 * Q. getMember()를 public으로 두는 이유는?
 * A. HealthRecordService, FeedbackService 등 다른 Service에서
 *    "회원 존재 여부 확인 + Entity 조회"를 재사용하기 위해서입니다.
 *    없으면 ResourceNotFoundException(404)를 던집니다.
 *
 * Q. 계정에 연결된 회원을 삭제하면?
 * A. accounts.member_id FK 때문에 DB 제약 위반(500)이 날 수 있다.
 *    삭제 전에 existsByMember_Id로 막고 InvalidRequestException(400)을 반환한다.
 */
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    public MemberService(
            MemberRepository memberRepository,
            AccountRepository accountRepository
    ) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * [공부/면접] 전체 회원 목록을 조회해 응답 DTO 리스트로 변환합니다.
     * Entity를 API에 그대로 노출하지 않고 MemberResponse.from()으로 변환합니다.
     */
    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
    }

    /**
     * [공부/면접] ID로 회원 한 명을 조회합니다. 없으면 getMember()에서 404 예외가 발생합니다.
     */
    public MemberResponse findById(Long id) {
        return MemberResponse.from(getMember(id));
    }

    /**
     * [공부/면접] 다른 Service에서 재사용하는 "회원 Entity 조회" 헬퍼입니다.
     *
     * 면접 포인트: Optional.orElseThrow()로 null 체크와 예외 처리를 한 줄로 표현합니다.
     *             ResourceNotFoundException → GlobalExceptionHandler → HTTP 404
     */
    public Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
    }

    /**
     * [공부/면접] 새 회원을 등록합니다.
     *
     * 흐름: Request DTO → Member Entity 생성(서버 기본값 status, lastMeasuredDate 설정)
     *       → memberRepository.save(INSERT) → MemberResponse 반환
     *
     * 면접 포인트: @Transactional(쓰기)로 INSERT가 원자적으로 커밋됩니다.
     */
    @Transactional
    public MemberResponse create(MemberCreateRequest request) {

        Member member = new Member(
                request.name(), request.gender(), request.age(),
                request.height(), request.weight(), request.goal(), request.progress(),
                MemberStatus.CHECK_REQUIRED,
                null

        );

        Member saved = memberRepository.save(member);

        return MemberResponse.from(saved);

    }

    /**
     * [공부/면접] 기존 회원의 목표·진행률을 수정합니다.
     *
     * 흐름: getMember()로 영속 Entity 조회 → changeGoal()로 필드 변경
     *       → save() 없이 트랜잭션 커밋 시 JPA dirty checking으로 UPDATE
     *
     * 면접 포인트: @Transactional 안에서 조회한 Entity는 1차 캐시에 있어
     *             변경 감지 후 자동 UPDATE됩니다(명시적 save 불필요).
     */
    @Transactional
    public MemberResponse update(Long memberId, MemberUpdateRequest request) {
        Member member = getMember(memberId);

        member.changeGoal(request.goal(), request.progress());

        return MemberResponse.from(member);
    }

    /**
     * [공부/면접] 회원을 삭제합니다.
     * 계정에 연결된 회원이면 FK 위반(500) 대신 400으로 거절합니다.
     */
    @Transactional
    @SuppressWarnings("null")
    public void delete(Long memberId) {
        Member member = getMember(memberId);

        if (accountRepository.existsByMember_Id(memberId)) {
            throw new InvalidRequestException(
                    "계정에 연결된 회원은 삭제할 수 없습니다."
            );
        }

        memberRepository.delete(member);
    }
}
