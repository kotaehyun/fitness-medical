package com.fitnessmedical.dto.health;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * [공부/면접] 건강 기록 등록 요청 DTO
 *
 * <p><b>Q. memberId가 DTO에 없는 이유는?</b><br>
 * A. REST 경로 {@code /members/{memberId}/health-records}에서 받습니다.
 * Entity(HealthRecord)는 {@link com.fitnessmedical.entity.Member} 객체 FK로 저장.</p>
 *
 * <p><b>Q. @Min/@Max vs @DecimalMin/@DecimalMax?</b><br>
 * A. int 등 정수 → @Min/@Max. double 등 소수 → @DecimalMin/@DecimalMax(문자열로 경계값 표현).</p>
 *
 * <p><b>Q. @NotNull vs @NotBlank?</b><br>
 * A. LocalDate는 @NotNull(날짜 미전달 방지). String 필드가 있다면 @NotBlank 사용.</p>
 */
public record HealthRecordRequest(
        @NotNull LocalDate measuredDate,

        @Min(80) @Max(180) int systolic,
        @Min(50) @Max(120) int diastolic,
        @Min(60) @Max(200) int bloodSugar,

        @DecimalMin("30.0") @DecimalMax("200.0") double weight,
        @DecimalMin("5.0") @DecimalMax("60.0") double bodyFat,
        @DecimalMin("0.0") @DecimalMax("16.0") double sleepHours,

        @Min(0) @Max(50000) int steps
) {
}
