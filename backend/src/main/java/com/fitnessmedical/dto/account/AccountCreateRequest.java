package com.fitnessmedical.dto.account;

import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.entity.ProfessionalType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * [공부/면접] 계정 생성 요청 DTO (Java record)
 *
 * <p><b>Q. record를 DTO로 쓰면 좋은 점은?</b><br>
 * A. 불변(immutable) 데이터 캐리어, equals/hashCode/toString/접근자 자동 생성.
 * JSON 역직렬화(@RequestBody)와 Bean Validation 조합에 적합합니다.</p>
 *
 * <p><b>Q. Entity(Account)와 무엇이 다른가?</b><br>
 * A. DTO는 API 입력·검증 전용. password는 평문으로 받아 Service에서 BCrypt 인코딩 후 Entity에 저장.
 * JPA 연관관계(Member 객체) 대신 {@code memberId}(Long)만 전달합니다.</p>
 *
 * <p><b>Q. memberId와 role 규칙은?</b><br>
 * A. 공개 가입에서는 {@code memberId}를 받지 않는다. 보내면 400.
 * {@link AccountRole#MEMBER} → 프로필(gender/age 등)로 새 Member 생성.
 * {@link AccountRole#PROFESSIONAL} → {@code professionalType} 필수, 가입 직후 verified=false.
 * 전문의 {@code licenseNumber} 필수, 트레이너 자격번호는 우대(선택).</p>
 *
 * <p><b>Q. @NotBlank vs @NotNull?</b><br>
 * A. @NotBlank는 String 전용 — null, "", 공백만 있는 문자열 거부.
 * @NotNull은 null만 거부(빈 문자열은 통과).</p>
 */
public record AccountCreateRequest(

        @NotBlank(message = "로그인 아이디는 필수입니다.")
        @Size(min = 4, max = 50, message = "로그인 아이디는 4자 이상 50자 이하여야 합니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9_]+$",
                message = "로그인 아이디는 영문, 숫자, 밑줄만 사용할 수 있습니다."
        )
        String loginId,

        // 평문으로 전달 — Entity 저장 전 BCrypt 인코딩. 로깅·응답에 절대 포함하지 않음
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하여야 합니다.")
        String password,

        @NotBlank(message = "표시 이름은 필수입니다.")
        @Size(max = 30, message = "표시 이름은 30자 이하여야 합니다.")
        String displayName,

        @NotNull(message = "계정 역할은 필수입니다.")
        AccountRole role,

        // 공개 가입에서는 사용하지 않음. 값이 오면 Service가 400. @Positive는 0·음수 학습용
        @Positive(message = "회원 ID는 양수여야 합니다.")
        Long memberId,

        ProfessionalType professionalType,

        @Size(max = 20, message = "면허번호는 20자 이하여야 합니다.")
        String licenseNumber,

        @Size(max = 10, message = "성별은 10자 이하여야 합니다.")
        String gender,

        @Min(1) @Max(120)
        Integer age,

        @DecimalMin("140") @DecimalMax("200")
        Double height,

        @DecimalMin("30.0") @DecimalMax("200.0")
        Double weight,

        @Size(max = 100, message = "목표는 100자 이하여야 합니다.")
        String goal,

        @Min(0) @Max(100)
        Integer progress
) {
}
