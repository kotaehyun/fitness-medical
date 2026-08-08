package com.fitnessmedical.repository;

import com.fitnessmedical.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * [공부/면접] HealthRecord Entity용 Spring Data JPA Repository
 *
 * <p><b>Q. findByMemberIdOrderByMeasuredDateDesc 네이밍 규칙?</b><br>
 * A. findBy + 속성명(MemberId → member.id FK) + OrderBy + 필드 + Desc/ Asc.
 * 파싱되어 {@code WHERE member_id = ? ORDER BY measured_date DESC} JPQL/SQL 생성.</p>
 *
 * <p><b>Q. List vs Optional?</b><br>
 * A. 0~N건 조회는 List(빈 리스트). 단건은 Optional. exists → boolean.</p>
 */
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {

    // 회원별 최신 측정일 순 건강 기록 목록
    List<HealthRecord> findByMemberIdOrderByMeasuredDateDesc(Long memberId);
}
