package com.fitnessmedical.common;

import java.time.LocalDateTime;

/**
 * [공부/면접] API 오류 응답 DTO (Java record)
 *
 * Q. record를 쓰는 이유?
 * A. timestamp·status·message만 담는 불변 값 객체 — equals/hashCode/toString/생성자가 자동 생성된다.
 *    오류 응답처럼 필드가 고정된 DTO에 보일러플레이트를 줄이기 좋다.
 *
 * Q. HTTP status를 body에도 넣는 이유?
 * A. 일부 클라이언트·프록시는 status line만 보거나 body를 먼저 파싱한다.
 *    JSON 본문에 status를 포함하면 로그·디버깅 시 한 객체로 오류 맥락을 확인할 수 있다.
 *
 * Q. GlobalExceptionHandler와의 관계?
 * A. 모든 @ExceptionHandler가 이 record를 body로 반환해 오류 형식을 통일한다.
 *    성공 응답 DTO와 분리해 "실패 시 계약"을 명확히 한다.
 */
public record ApiErrorResponse(LocalDateTime timestamp, int status, String message) {
}
