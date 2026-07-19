package com.fitnessmedical.service;

import com.fitnessmedical.dto.health.HealthRecordRequest;
import com.fitnessmedical.dto.health.HealthRecordResponse;
import com.fitnessmedical.entity.HealthRecord;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.repository.HealthRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** 건강 기록 조회와 등록 규칙을 처리하는 Service입니다. */
@Service
@Transactional(readOnly = true)
public class HealthRecordService {

    private final HealthRecordRepository healthRecordRepository;
    private final MemberService memberService;

    public HealthRecordService(HealthRecordRepository healthRecordRepository, MemberService memberService) {
        this.healthRecordRepository = healthRecordRepository;
        this.memberService = memberService;
    }

    public List<HealthRecordResponse> findByMemberId(Long memberId) {
        // 기록을 조회하기 전에 회원이 실제로 존재하는지 먼저 확인합니다.
        memberService.getMember(memberId);
        return healthRecordRepository.findByMemberIdOrderByMeasuredDateDesc(memberId).stream()
                .map(HealthRecordResponse::from)
                .toList();
    }

    // 클래스에는 readOnly=true가 적용되어 있으므로 저장 메서드에서 일반 트랜잭션으로 다시 지정합니다.
    @Transactional
    public HealthRecordResponse create(Long memberId, HealthRecordRequest request) {
        Member member = memberService.getMember(memberId);

        // Controller에서 받은 DTO 값을 Entity로 옮깁니다.
        // Entity는 DB 저장용이고 Request는 API 입력용이므로 역할을 구분합니다.
        HealthRecord record = new HealthRecord(
                member, request.measuredDate(), request.systolic(), request.diastolic(),
                request.bloodSugar(), request.weight(), request.bodyFat(),
                request.sleepHours(), request.steps()
        );
        // save()가 INSERT를 수행하고, 저장된 Entity를 다시 응답 DTO로 변환합니다.
        return HealthRecordResponse.from(healthRecordRepository.save(record));
    }
}
