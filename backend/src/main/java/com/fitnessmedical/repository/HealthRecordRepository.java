package com.fitnessmedical.repository;

import com.fitnessmedical.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/** 건강 기록 데이터 접근 계층입니다. */
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    // Spring Data JPA의 Query Method입니다.
    // 메서드 이름을 분석해서 memberId 조건과 measuredDate 내림차순 쿼리를 자동 생성합니다.
    List<HealthRecord> findByMemberIdOrderByMeasuredDateDesc(Long memberId);
}
