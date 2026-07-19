package com.fitnessmedical.entity;

public enum MemberStatus {
    GOOD("양호"),
    CAUTION("주의"),
    CHECK_REQUIRED("확인 필요");

    private final String label;

    MemberStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
