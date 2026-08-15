package com.fitnessmedical.dto.admin;

import java.time.LocalDate;

import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.Member;

/**
 * [공부/면접] 관리자용 일반 회원 가입 현황 DTO
 *
 * <p>password·키·체중은 넣지 않는다. 가입 여부·상태·목표만 본다.</p>
 */
public record AdminMemberResponse(
        Long accountId,
        String loginId,
        String displayName,
        Long memberId,
        String gender,
        Integer age,
        String goal,
        Integer progress,
        String status,
        LocalDate lastMeasuredDate
) {
    public static AdminMemberResponse from(Account account) {
        Member member = account.getMember();
        return new AdminMemberResponse(
                account.getId(),
                account.getLoginId(),
                account.getDisplayName(),
                member == null ? null : member.getId(),
                member == null ? null : member.getGender(),
                member == null ? null : member.getAge(),
                member == null ? null : member.getGoal(),
                member == null ? null : member.getProgress(),
                member == null || member.getStatus() == null ? null : member.getStatus().getLabel(),
                member == null ? null : member.getLastMeasuredDate()
        );
    }
}
