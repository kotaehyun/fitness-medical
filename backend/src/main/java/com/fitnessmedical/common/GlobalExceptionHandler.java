package com.fitnessmedical.common;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * [공부/면접] 전역 예외 처리 (@RestControllerAdvice)
 *
 * Q. @RestControllerAdvice vs @ControllerAdvice?
 * A. @RestControllerAdvice = @ControllerAdvice + @ResponseBody.
 *    반환값이 View가 아니라 JSON 등 직렬화 body로 나가 REST API에 맞다.
 *
 * Q. @ExceptionHandler 동작 원리?
 * A. Controller·Service에서 던진 예외가 DispatcherServlet까지 전파되면,
 *    타입이 맞는 Handler 메서드가 잡아 ResponseEntity(상태코드 + ApiErrorResponse)로 변환한다.
 *
 * Q. Controller마다 try-catch 안 해도 되는 이유?
 * A. 관심사 분리 — Controller는 정상 흐름·HTTP 매핑, Handler는 오류 → HTTP 변환을 담당.
 *    예외 타입별 상태코드(404/409/401/400)를 한곳에서 유지할 수 있다.
 *
 * Q. 예외 → HTTP 매핑 요약?
 * A. ResourceNotFoundException → 404, DuplicateResourceException → 409,
 *    InvalidCredentialsException → 401, ForbiddenException → 403,
 *    InvalidRequestException → 400, MethodArgumentNotValidException(@Valid) → 400.
 *
 * 여러 Controller에서 발생하는 예외를 한 곳에서 처리합니다.
 * 같은 오류 응답 형식을 반복 작성하지 않기 위한 클래스입니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 도메인 "없음" → 404: 클라이언트가 잘못된 ID를 요청했음을 명확히 전달
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(LocalDateTime.now(), 404, exception.getMessage()));
    }

    // 고유 값 중복 → 409: 재시도·다른 loginId 선택을 유도 (생성 요청과 충돌)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicate(
            DuplicateResourceException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.CONFLICT.value(),
                        exception.getMessage()
                ));
    }

    // 자격 증명 불일치 → 401: 인증 실패(재로그인 필요). 403과 구분
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException exception
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        exception.getMessage()
                ));
    }

    // 권한·소유권 없음 → 403: 로그인은 됐지만 이 자원은 불가
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(
            ForbiddenException exception
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.FORBIDDEN.value(),
                        exception.getMessage()
                ));
    }

    // 도메인 규칙 위반 → 400: 요청 본문·파라미터 값을 수정하도록 안내
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRequest(
                InvalidRequestException exception
    ) {
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        exception.getMessage()
                ));
    }

    // FastAPI·Ollama 불가 → 503: 의존 서비스 문제. 클라이언트 입력 오류(4xx)와 구분
    @ExceptionHandler(AiServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleAiUnavailable(
            AiServiceUnavailableException exception
    ) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        exception.getMessage()
                ));
    }

    // @Valid 실패 → 400: Bean Validation은 Controller 진입 전/직후에 Spring이 검사
    // Service까지 내려가지 않으므로 "입력 형식 오류"와 "비즈니스 400"을 Handler에서 같은 status로 통일
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        // 첫 번째 필드 오류만 반환 — 여러 오류를 한 번에 보여주려면 구조를 확장하면 됨
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("입력값을 확인해 주세요.");

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(LocalDateTime.now(), 400, message));
    }


}
