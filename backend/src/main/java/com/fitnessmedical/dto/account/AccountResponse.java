package com.fitnessmedical.dto.account;

import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;

public record AccountResponse (
        Long id,
        String loginId,
        String displayName,
        AccountRole role
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getLoginId(),
                account.getDisplayName(),
                account.getRole()
        );
    }
}
