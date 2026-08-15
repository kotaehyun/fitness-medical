package com.fitnessmedical.common;

/**
 * [공부/면접] FastAPI / Ollama 일시 불가
 *
 * <p>Q. 왜 503인가?<br>
 * A. 클라이언트 요청이 잘못된 것이 아니라(4xx), 의존 서비스가 꺼졌거나 타임아웃난 경우다.
 * Handler에서 SERVICE_UNAVAILABLE로 매핑한다.</p>
 */
public class AiServiceUnavailableException extends RuntimeException {

    public AiServiceUnavailableException(String message) {
        super(message);
    }

    public AiServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
