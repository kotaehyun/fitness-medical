package com.fitnessmedical.dto.health;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * 건강 기록 등록 요청 JSON을 받는 DTO입니다.
 * 각 어노테이션은 허용할 입력 범위를 검증합니다.
 */
public record HealthRecordRequest(
        // @NotNull은 값 자체가 전달되지 않은 경우를 막습니다.
        @NotNull LocalDate measuredDate,
        // @Min과 @Max는 숫자의 최솟값과 최댓값을 제한합니다.
        @Min(80) @Max(180) int systolic,
        @Min(50) @Max(120) int diastolic,
        @Min(60) @Max(200) int bloodSugar,
        // 소수 범위는 DecimalMin과 DecimalMax로 표현할 수 있습니다.
        @DecimalMin("30.0") @DecimalMax("200.0") double weight,
        @DecimalMin("5.0") @DecimalMax("60.0") double bodyFat,
        @DecimalMin("0.0") @DecimalMax("16.0") double sleepHours,
        @Min(0) @Max(50000) int steps
) {
}
