package com.fitnessmedical.entity;

/**
 * [공부/면접] 계정 역할 Enum
 *
 * <p><b>Q. MEMBER·PROFESSIONAL·ADMIN의 memberId 규칙은?</b><br>
 * A. {@link #MEMBER} → 공개 가입 시 새 {@link Member}를 만들고 1:1 연결.<br>
 * {@link #PROFESSIONAL} → {@code member}는 null. 공개 가입은 verified=false.<br>
 * {@link #ADMIN} → {@code member}는 null. 공개 가입 불가(시드만). 전문직 승인/해제.</p>
 *
 * <p><b>Q. Enum을 Entity에 어떻게 저장하나?</b><br>
 * A. {@code @Enumerated(EnumType.STRING)}으로 컬럼에 이름 문자열 저장.
 * ORDINAL(0,1,2…)은 enum 순서 변경 시 DB 값이 깨질 수 있어 STRING을 권장합니다.</p>
 */
public enum AccountRole {
    // 일반 회원 — Account.member FK로 Member 1:1 연결 필수
    MEMBER,
    // 트레이너·전문의 — Account.member는 null. 세부 유형은 ProfessionalType
    PROFESSIONAL,
    // 슈퍼계정 — 공개 가입 불가. 전문직 professionalVerified 승인/해제만
    ADMIN
}
