package com.fitnessmedical.service;

import com.fitnessmedical.dto.health.HealthRecordRequest;
import com.fitnessmedical.dto.health.HealthRecordResponse;
import com.fitnessmedical.entity.HealthRecord;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.repository.HealthRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * [공부/면접] 회원별 건강 측정 기록 조회·등록을 담당하는 Service 계층입니다.
 *
 * Q. HealthRecordRepository를 직접 쓰면서 MemberService도 주입하는 이유는?
 * A. 기록 CRUD 전에 "해당 memberId 회원이 존재하는지"를 memberService.getMember()로
 *    검증합니다. 존재하지 않으면 ResourceNotFoundException(404) — 빈 목록 대신 명확한 오류.
 *
 * Q. 계층 구조는?
 * A. MemberController → HealthRecordService → HealthRecordRepository
 *    (회원 존재 확인은 MemberService → MemberRepository)
 *
 * 예외: ResourceNotFoundException(404) — getMember()에서 발생
 */
@Service
@Transactional(readOnly = true)
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final MemberService memberService;

    public HealthRecordService(HealthRecordRepository healthRecordRepository, MemberService memberService) {
        this.healthRecordRepository = healthRecordRepository;
        this.memberService = memberService;
    }

    /**
     * [공부/면접] 특정 회원의 건강 기록을 최신 측정일 순으로 조회합니다.
     *
     * 흐름: memberService.getMember(memberId)로 회원 존재 확인(404)
     *       → healthRecordRepository 조회 → HealthRecordResponse 리스트 변환
     */
    public List<HealthRecordResponse> findByMemberId(Long memberId) {
        memberService.getMember(memberId);
        return healthRecordRepository.findByMemberIdOrderByMeasuredDateDesc(memberId).stream()
                .map(HealthRecordResponse::from)
                .toList();
    }

    /**
     * [공부/면접] 건강 측정 기록을 새로 등록합니다.
     *
     * 흐름: getMember() → HealthRecordRequest DTO를 Entity로 변환
     *       → save(INSERT) → HealthRecordResponse 반환
     *
     * 면접 포인트: @Transactional(쓰기) — Member 조회와 Record INSERT가 한 트랜잭션.
     *             Request/Response DTO와 Entity 역할 분리(API 스키마 ≠ DB 스키마).
     */
    @Transactional
    public HealthRecordResponse create(Long memberId, HealthRecordRequest request) {
        Member member = memberService.getMember(memberId);

        HealthRecord record = new HealthRecord(
                member, request.measuredDate(), request.systolic(), request.diastolic(),
                request.bloodSugar(), request.weight(), request.bodyFat(),
                request.sleepHours(), request.steps()
        );
        return HealthRecordResponse.from(healthRecordRepository.save(record));
    }


}
