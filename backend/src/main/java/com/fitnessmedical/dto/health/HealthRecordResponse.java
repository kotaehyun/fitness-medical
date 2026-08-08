package com.fitnessmedical.dto.health;

import com.fitnessmedical.entity.HealthRecord;
import java.time.LocalDate;

/**
 * [공부/면접] 건강 기록 조회 응답 DTO
 *
 * <p><b>Q. Entity 대신 DTO로 memberId만 노출하는 이유는?</b><br>
 * A. JSON에 Member 전체 객체·JPA 프록시를 실어 보내지 않기 위함.
 * 클라이언트는 FK id만 알면 충분합니다.</p>
 *
 * <p><b>Q. from(HealthRecord)에서 getMember().getId() 호출 시 주의점?</b><br>
 * A. member가 LAZY이므로 트랜잭션(@Transactional) 안에서 호출해야
 * LazyInitializationException을 피할 수 있습니다.</p>
 */
public record HealthRecordResponse(
        Long id,
        Long memberId,
        LocalDate measuredDate,
        int systolic,
        int diastolic,
        int bloodSugar,
        double weight,
        double bodyFat,
        double sleepHours,
        int steps
) {
    public static HealthRecordResponse from(HealthRecord record) {
        return new HealthRecordResponse(
                record.getId(), record.getMember().getId(), record.getMeasuredDate(),
                record.getSystolic(), record.getDiastolic(), record.getBloodSugar(),
                record.getWeight(), record.getBodyFat(), record.getSleepHours(), record.getSteps()
        );
    }
}
