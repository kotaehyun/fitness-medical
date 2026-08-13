package com.fitnessmedical.entity;

/**
 * [공부/면접] PROFESSIONAL 계정의 세부 유형
 *
 * Q. 왜 AccountRole을 TRAINER/PHYSICIAN로 쪼개지 않나?
 * A. 권한(피드백 작성)은 둘 다 PROFESSIONAL이다. 세부 유형만 ProfessionalType으로 나눈다.
 *
 * Q. 자격/면허는 둘 다 필수인가?
 * A. 전문의 면허만 필수. 트레이너 생활스포츠지도사 자격은 우대(선택).
 *
 * Q. 실제 국가 자격/면허 조회인가?
 * A. 아니다. 학습용 형식 검증이다. 진단·처방 권한을 주지 않는다.
 *    트레이너 → 생활스포츠지도사 자격번호, 전문의 → 의사 면허번호.
 */
public enum ProfessionalType {
    TRAINER("트레이너"),
    PHYSICIAN("전문의");

    private final String label;

    ProfessionalType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
