package com.fitnessmedical.common;

/**
 * [공부/면접] 도메인 예외 — 고유 값 중복(회원가입 loginId 등)
 *
 * Q. 409 CONFLICT vs 400 BAD_REQUEST?
 * A. 409는 "요청은 문법적으로 맞지만, 현재 서버 상태와 충돌"(이미 존재하는 loginId).
 *    400은 필드 형식 오류·비즈니스 규칙 위반(InvalidRequestException)에 가깝다.
 *
 * Q. DB unique constraint 위반과의 관계?
 * A. Service에서 save 전 findByLoginId()로 먼저 검사해 이 예외를 던진다.
 *    Handler가 409로 변환하므로 클라이언트는 "중복"임을 상태 코드만으로도 알 수 있다.
 *
 * Q. 왜 별도 예외 클래스?
 * A. IllegalStateException 등 범용 타입과 구분해 Handler에서 HTTP 상태·메시지를 정확히 매핑하기 위함.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
