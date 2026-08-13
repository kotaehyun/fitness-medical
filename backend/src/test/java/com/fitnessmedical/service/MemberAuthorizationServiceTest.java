package com.fitnessmedical.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fitnessmedical.common.ForbiddenException;
import com.fitnessmedical.common.InvalidCredentialsException;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.repository.AccountRepository;

class MemberAuthorizationServiceTest {

    final AccountRepository accountRepository = mock(AccountRepository.class);
    final MemberAuthorizationService authorizationService =
            new MemberAuthorizationService(accountRepository);

    @Test
    @DisplayName("PROFESSIONAL은 모든 memberId에 접근할 수 있다.")
    void professional_canAccessAnyMember() {
        Account professional = new Account(
                "pro01",
                "encoded",
                "김전문가",
                AccountRole.PROFESSIONAL,
                null
        );
        given(accountRepository.findByLoginId("pro01")).willReturn(Optional.of(professional));

        authorizationService.assertCanAccessMember("pro01", 99L);
        assertThat(authorizationService.requireProfessional("pro01")).isSameAs(professional);
    }

    @Test
    @DisplayName("MEMBER는 연결된 본인 memberId만 접근할 수 있다.")
    void member_canAccessOwnMemberOnly() {
        Member linked = mock(Member.class);
        given(linked.getId()).willReturn(1L);
        Account member = new Account(
                "member01",
                "encoded",
                "홍길동",
                AccountRole.MEMBER,
                linked
        );
        given(accountRepository.findByLoginId("member01")).willReturn(Optional.of(member));

        authorizationService.assertCanAccessMember("member01", 1L);

        assertThatThrownBy(() -> authorizationService.assertCanAccessMember("member01", 2L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("해당 회원 데이터에 접근할 수 없습니다.");
        assertThatThrownBy(() -> authorizationService.requireProfessional("member01"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("전문가 권한이 필요합니다.");
    }

    @Test
    @DisplayName("세션 loginId가 없으면 401 계열로 거절한다.")
    void missingLogin_throwsInvalidCredentials() {
        assertThatThrownBy(() -> authorizationService.requireAccount(null))
                .isInstanceOf(InvalidCredentialsException.class);
        given(accountRepository.findByLoginId("ghost")).willReturn(Optional.empty());
        assertThatThrownBy(() -> authorizationService.requireAccount("ghost"))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
