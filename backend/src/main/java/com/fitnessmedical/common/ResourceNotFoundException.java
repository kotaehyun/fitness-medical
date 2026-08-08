package com.fitnessmedical.common;

/**
 * [공부/면접] 도메인 예외 — 리소스를 찾을 수 없을 때
 *
 * Q. 왜 RuntimeException(unchecked)을 쓰나?
 * A. "회원 ID가 없음"은 비즈니스 규칙 위반이지, 컴파일러가 강제할 I/O 오류가 아니다.
 *    Service에서 throws 선언 없이 던져도 되고, GlobalExceptionHandler가 HTTP로 변환한다.
 *
 * Q. 예외 → HTTP 매핑은 어디서?
 * A. GlobalExceptionHandler.handleNotFound()가 이 타입을 잡아 404 NOT_FOUND로 응답한다.
 *    Controller마다 try-catch를 두지 않아도 클라이언트는 일관된 JSON(ApiErrorResponse)을 받는다.
 *
 * Q. 404 vs 400 차이?
 * A. 404는 "요청한 리소스 자체가 없음"(예: 존재하지 않는 memberId).
 *    400은 "요청 형식·값이 잘못됨"(InvalidRequestException, @Valid 실패).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        // message는 ApiErrorResponse.message로 그대로 전달 — 사용자에게 이유를 알려주기 위함
        super(message);
    }
}
