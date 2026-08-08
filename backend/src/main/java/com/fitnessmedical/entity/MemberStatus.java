package com.fitnessmedical.entity;

/**
 * [공부/면접] 회원 건강 상태 Enum
 *
 * <p><b>Q. DB에는 enum을 어떻게 저장하나?</b><br>
 * A. {@link Member}에서 {@code @Enumerated(EnumType.STRING)} 사용 →
 * {@code GOOD}, {@code CAUTION} 등 상수 이름이 VARCHAR로 저장됩니다.</p>
 *
 * <p><b>Q. label 필드는 왜 있나?</b><br>
 * A. API 응답({@link com.fitnessmedical.dto.member.MemberResponse})에서
 * enum 이름 대신 한글 표시("양호")를 내려주기 위한 도메인 라벨입니다.</p>
 */
public enum MemberStatus {
    GOOD("양호"),
    CAUTION("주의"),
    CHECK_REQUIRED("확인 필요");

    // 화면/API용 한글 라벨 — DB에는 enum 이름(GOOD 등)만 저장
    private final String label;

    MemberStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
