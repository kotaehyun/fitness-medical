package com.fitnessmedical.common;

/**
 * [공부/면접] 도메인 예외 — 비즈니스 규칙상 처리할 수 없는 요청
 *
 * Q. 400 BAD_REQUEST vs MethodArgumentNotValidException(@Valid)?
 * A. @Valid는 DTO 필드 형식(NotBlank, Size 등) 위반 시 Spring이 자동 발생.
 *    InvalidRequestException은 "형식은 맞지만 도메인 규칙 위반"(예: 허용되지 않는 역할 값)에 쓴다.
 *
 * Q. ResourceNotFoundException(404)과 구분?
 * A. 404는 대상 리소스가 DB에 없음. 400은 클라이언트가 보낸 값·조합이 규칙에 맞지 않음.
 *
 * Q. Handler에서 badRequest()를 쓰는 이유?
 * A. HttpStatus.BAD_REQUEST(400)와 ApiErrorResponse.status를 일치시켜
 *    프론트엔드가 status 필드만으로도 오류 종류를 분기할 수 있게 한다.
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
