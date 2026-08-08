package com.fitnessmedical.entity;

/**
 * [공부/면접] 계정 역할 Enum
 *
 * <p><b>Q. MEMBER와 PROFESSIONAL의 memberId 규칙은?</b><br>
 * A. {@link #MEMBER} → 계정 생성 시 {@code memberId} 필수 (연결된 {@link Member} 존재).<br>
 * {@link #PROFESSIONAL} → {@code memberId}는 null이어야 함 (전문가는 특정 회원에 묶이지 않음).</p>
 *
 * <p><b>Q. Enum을 Entity에 어떻게 저장하나?</b><br>
 * A. {@code @Enumerated(EnumType.STRING)}으로 컬럼에 이름 문자열 저장.
 * ORDINAL(0,1,2…)은 enum 순서 변경 시 DB 값이 깨질 수 있어 STRING을 권장합니다.</p>
 */
public enum AccountRole {
    // 일반 회원 — Account.member FK로 Member 1:1 연결 필수
    MEMBER,
    // 재활·운동 전문가 — Account.member는 null
    PROFESSIONAL
}
