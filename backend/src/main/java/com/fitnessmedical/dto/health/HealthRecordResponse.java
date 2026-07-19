package com.fitnessmedical.dto.health;

import com.fitnessmedical.entity.HealthRecord;
import java.time.LocalDate;

public record HealthRecordResponse(
        Long id, Long memberId, LocalDate measuredDate,
        int systolic, int diastolic, int bloodSugar,
        double weight, double bodyFat, double sleepHours, int steps
) {
    public static HealthRecordResponse from(HealthRecord record) {
        return new HealthRecordResponse(
                record.getId(), record.getMember().getId(), record.getMeasuredDate(),
                record.getSystolic(), record.getDiastolic(), record.getBloodSugar(),
                record.getWeight(), record.getBodyFat(), record.getSleepHours(), record.getSteps()
        );
    }
}
