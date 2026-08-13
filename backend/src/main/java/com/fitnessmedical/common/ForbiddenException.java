package com.fitnessmedical.common;

/**
 * [공부/면접] 도메인 예외 — 인증은 됐지만 권한·소유권이 없음
 *
 * Q. 401과 403 차이는?
 * A. 401은 로그인 자체가 안 됨. 403은 로그인은 됐지만 타인 데이터·전문가 전용 API.
 *
 * Q. InvalidRequestException(400)과 구분?
 * A. 400은 요청 값/조합이 규칙에 안 맞음. 403은 “이 계정으로는 이 자원에 접근 불가”.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
