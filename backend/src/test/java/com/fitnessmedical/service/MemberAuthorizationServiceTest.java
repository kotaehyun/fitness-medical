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
import com.fitnessmedical.entity.ProfessionalType;
import com.fitnessmedical.repository.AccountRepository;

class MemberAuthorizationServiceTest {

    final AccountRepository accountRepository = mock(AccountRepository.class);
    final MemberAuthorizationService authorizationService =
            new MemberAuthorizationService(accountRepository);

    @Test
    @DisplayName("인증된 PROFESSIONAL은 모든 memberId에 접근할 수 있다.")
    void verifiedProfessional_canAccessAnyMember() {
        Account professional = new Account(
                "pro01",
                "encoded",
                "김전문가",
                AccountRole.PROFESSIONAL,
                null,
                ProfessionalType.TRAINER,
                null,
                true
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
    @DisplayName("미인증 트레이너는 전문가 API를 쓸 수 없다.")
    void unverifiedTrainer_isForbidden() {
        Account trainer = new Account(
                "trainer02",
                "encoded",
                "미등록트레이너",
                AccountRole.PROFESSIONAL,
                null,
                ProfessionalType.TRAINER,
                null,
                false
        );
        given(accountRepository.findByLoginId("trainer02")).willReturn(Optional.of(trainer));

        assertThatThrownBy(() -> authorizationService.requireProfessional("trainer02"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("전문직 인증이 완료되지 않았습니다.");
        assertThatThrownBy(() -> authorizationService.assertCanAccessMember("trainer02", 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("전문직 인증이 완료되지 않았습니다.");
    }

    @Test
    @DisplayName("면허 인증이 안 된 전문의는 거절한다.")
    void unverifiedPhysician_isForbidden() {
        Account physician = new Account(
                "doctor02",
                "encoded",
                "미인증전문의",
                AccountRole.PROFESSIONAL,
                null,
                ProfessionalType.PHYSICIAN,
                null,
                false
        );
        given(accountRepository.findByLoginId("doctor02")).willReturn(Optional.of(physician));

        assertThatThrownBy(() -> authorizationService.requireProfessional("doctor02"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("전문직 인증이 완료되지 않았습니다.");
        assertThatThrownBy(() -> authorizationService.assertCanAccessMember("doctor02", 1L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("전문직 인증이 완료되지 않았습니다.");
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
