package com.fitnessmedical.repository;

import com.fitnessmedical.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * [공부/면접] Feedback Entity용 Spring Data JPA Repository
 *
 * <p><b>Q. findByMemberIdOrderByWrittenDateDesc는?</b><br>
 * A. HealthRecordRepository와 동일한 Query Method 패턴.
 * memberId 조건 + writtenDate 내림차순(최신 피드백 우선).</p>
 *
 * <p><b>Q. @Query 없이 메서드 이름만으로 충분한가?</b><br>
 * A. 단순 조건·정렬은 네이밍 규칙으로 자동 생성. 복잡 JOIN·집계는 @Query/JPQL.</p>
 */
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByMemberIdOrderByWrittenDateDesc(Long memberId);
}
