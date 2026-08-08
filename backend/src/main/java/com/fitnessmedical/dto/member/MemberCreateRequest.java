package com.fitnessmedical.dto.member;

import jakarta.validation.constraints.*;

/**
 * [공부/면접] 회원 등록 요청 DTO
 *
 * <p><b>Q. Entity(Member)와 필드 구성이 다른 이유는?</b><br>
 * A. {@code status}, {@code lastMeasuredDate}는 가입 시 측정 이력이 없어
 * MemberService.create()에서 기본값(CHECK_REQUIRED, null)으로 Entity에 설정합니다.</p>
 *
 * <p><b>Q. @NotBlank를 int/double에 쓸 수 없나?</b><br>
 * A. @NotBlank는 CharSequence(String) 전용. 숫자는 @Min/@Max, @DecimalMin/@DecimalMax로 범위 검증.</p>
 *
 * <p><b>Q. record DTO의 장점은?</b><br>
 * A. 불변 + 간결한 선언. @Valid와 함께 Controller에서 선언적 검증 가능.</p>
 */
public record MemberCreateRequest(
        // @NotBlank: null, "", 공백만 있는 문자열 모두 거부
        @NotNull @NotBlank String name,

        @NotNull @NotBlank String gender,

        // int는 원시 타입이라 null 불가 → @Min/@Max로 범위만 검사
        @Min(1) @Max(120) int age,

        @DecimalMin("30.0") @DecimalMax("200.0") double weight,

        @DecimalMin("140") @DecimalMax("200") double height,

        @NotNull @NotBlank String goal,

        @Min(0) @Max(100) int progress
) {
}
