package com.fitnessmedical.dto.member;

import jakarta.validation.constraints.*;

/**
 * [공부/면접] 회원 수정 요청 DTO
 *
 * <p><b>Q. CreateRequest와 UpdateRequest를 나누는 이유는?</b><br>
 * A. 수정 API는 goal·progress만 변경. 이름·나이 등은 별도 API/정책.
 * DTO를 분리하면 @Valid 검증 범위와 API 계약이 명확해집니다.</p>
 *
 * <p><b>Q. Entity의 changeGoal()과 어떻게 연결되나?</b><br>
 * A. Controller @Valid → Service → {@link com.fitnessmedical.entity.Member#changeGoal(String, int)}.</p>
 */
public record MemberUpdateRequest(

        @NotBlank String goal,

        @Min(0) @Max(100) int progress
) {
}
