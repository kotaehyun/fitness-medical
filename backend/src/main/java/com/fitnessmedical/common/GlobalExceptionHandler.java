package com.fitnessmedical.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

/**
 * 여러 Controller에서 발생하는 예외를 한 곳에서 처리합니다.
 * 같은 오류 응답 형식을 반복 작성하지 않기 위한 클래스입니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ResourceNotFoundException이 발생하면 HTTP 404 응답으로 변환합니다.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(LocalDateTime.now(), 404, exception.getMessage()));
    }

    // 로그인 아이디처럼 고유해야 하는 값이 중복되면 HTTP 409를 반환합니다.
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

    // @Valid 검증에 실패하면 Spring이 MethodArgumentNotValidException을 발생시킵니다.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("입력값을 확인해 주세요.");

        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse(LocalDateTime.now(), 400, message));
    }


}
