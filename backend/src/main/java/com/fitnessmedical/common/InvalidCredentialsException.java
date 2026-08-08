package com.fitnessmedical.common;

/**
 * [공부/면접] 도메인 예외 — 로그인 자격 증명 불일치
 *
 * Q. 401 UNAUTHORIZED vs 403 FORBIDDEN?
 * A. 401은 "누구인지 증명하지 못함"(아이디 없음, 비밀번호 불일치).
 *    403은 "인증은 됐지만 권한 없음"(예: MEMBER가 PROFESSIONAL 전용 API 호출).
 *
 * Q. "아이디 없음"과 "비밀번호 틀림"을 같은 메시지로?
 * A. 보안상 계정 존재 여부를 구분해 노출하지 않기 위함(타이밍·메시지 열거 공격 완화).
 *    Handler는 둘 다 401 + 동일한 ApiErrorResponse 형식으로 응답한다.
 *
 * Q. Spring Security AuthenticationException과의 차이?
 * A. 이 프로젝트는 Service 계층에서 도메인 예외를 던지고 Handler가 HTTP로 변환한다.
 *    Security 필터 체인 밖(REST Controller)에서도 동일한 오류 형식을 유지할 수 있다.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
