package com.fitnessmedical.dto.account;

import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;

/**
 * [공부/면접] 계정 조회/생성 응답 DTO
 *
 * <p><b>Q. Entity를 그대로 JSON으로 내려주면 안 되나?</b><br>
 * A. password·LAZY 연관 객체 등 민감·불필요 정보 노출, API와 DB 스키마 결합이 커집니다.
 * DTO로 필요한 필드(id, loginId, displayName, role, memberId)만 노출합니다.</p>
 *
 * <p><b>Q. password가 없는 이유는?</b><br>
 * A. 해시 포함 어떤 형태의 비밀번호도 클라이언트에 반환하면 안 됩니다.</p>
 *
 * <p><b>Q. from(Account) 정적 팩토리의 역할은?</b><br>
 * A. Entity → DTO 변환 로직을 한곳에 모아 Controller/Service 중복을 줄입니다.
 * {@code member}가 null(PROFESSIONAL)이면 memberId도 null로 매핑.</p>
 */
public record AccountResponse(
        Long id,
        String loginId,
        String displayName,
        AccountRole role,
        // MEMBER: 연결된 Member PK, PROFESSIONAL: null
        Long memberId
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getLoginId(),
                account.getDisplayName(),
                account.getRole(),
                account.getMember() == null ? null : account.getMember().getId()
        );
    }
}
