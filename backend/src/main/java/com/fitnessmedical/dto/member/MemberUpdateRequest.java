package com.fitnessmedical.dto.member;

import jakarta.validation.constraints.*;

/**
 * 회원 수정 요청 JSON을 받는 DTO입니다.
 *
 * 요구 사항 (STUDY_TASKS 2단계):
 * - 회원의 "목표(goal)"와 "진행률(progress)"만 수정 대상입니다.
 * - goal   : 자유 텍스트, 비어 있으면 안 됨
 * - progress : 0 ~ 100
 */

public record MemberUpdateRequest(

        @NotBlank String goal,

        @Min(0) @Max(100) int progress

) {
    
}
