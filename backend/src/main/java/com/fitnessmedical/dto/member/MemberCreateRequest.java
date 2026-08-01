package com.fitnessmedical.dto.member;



import jakarta.validation.constraints.*;


/**
 * 회원 등록 요청 DTO.
 *
 * 설계 결정:
 * - name, gender, age, height, weight, goal, progress
 *   → 회원가입 시 사용자가 직접 입력하는 값이라 DTO에 포함.
 * - status, lastMeasuredDate
 *   → 가입 시점엔 아직 측정 이력이 없어서 서버가 기본값을 정하는 게 자연스러움.
 *     DTO에는 넣지 않고, MemberService.create()에서 Member를 만들 때
 *     기본값(예: status = CHECK_REQUIRED, lastMeasuredDate = null)을 채워줍니다.
 * - quantity, joinDate
 *   → 요구사항에도 없고 엔티티에도 없는 필드라 삭제.
 */

public record MemberCreateRequest(
        // 이름: 필수. @NotBlank 하나로 null / "" / 공백만 있는 문자열까지 다 막아줍니다.
        // (String 전용 어노테이션이라 int, double, LocalDate엔 못 씁니다.)
        @NotNull @NotBlank String name,

        // 성별: name과 같은 이유로 @NotBlank
        @NotNull @NotBlank String gender,

        // 나이: 1~120, int는 애초에 null이 될 수 없어서 @NotBlank / @ NotNull이 아니라
        // 값의 "범위"를 검사하는 @Min/@Max를 씁니다.
        @Min(1) @Max(120) int age,


        // 체중(kg) : double 타입의 범위 제한은 @DecimalMin / DecimalMax를 씁니다.
        @DecimalMin("30.0") @DecimalMax("200.0") double weight, // 값은 30~200, 근데 변수명은 weight

        // 키(cm) : 위와 같은 이유로 @DecimalMin / @DecimalMax
        @DecimalMin("140") @DecimalMax("200") double height,  // 값은 140~200, 근데 변수명은 height

        // 목표: 자유 텍스트라 값이 비어있지 않은지만 체크
        @NotNull @NotBlank String goal,

        // 목표 진행률: 0~100, int라서 @Min/@Max
        @Min(0) @Max(100) int progress

) {

}
