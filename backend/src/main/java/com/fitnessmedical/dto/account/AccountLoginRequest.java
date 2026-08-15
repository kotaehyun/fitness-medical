package com.fitnessmedical.dto.account;

import jakarta.validation.constraints.NotBlank;

/**
 * [공부/면접] 로그인 요청 DTO
 *
 * <p><b>Q. AccountCreateRequest와 차이는?</b><br>
 * A. 로그인은 기존 계정 식별만 필요 → loginId + password 두 필드.
 * role·memberId·displayName 등은 서버가 DB에서 조회합니다.</p>
 *
 * <p><b>Q. password를 로그에 남기면?</b><br>
 * A. 절대 금지. 평문 비밀번호 로깅은 보안 사고입니다.
 * 인증 실패 시에도 "비밀번호 불일치"만 응답하고 값 자체는 기록하지 않습니다.</p>
 */
public record AccountLoginRequest(

        @NotBlank(message = "로그인 아이디는 필수입니다.")
        String loginId,

        // @NotBlank: null·빈 문자열 거부 — 인증 전 평문, 로깅·응답 금지
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
}
