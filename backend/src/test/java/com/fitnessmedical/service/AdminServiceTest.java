package com.fitnessmedical.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fitnessmedical.common.ForbiddenException;
import com.fitnessmedical.common.InvalidRequestException;
import com.fitnessmedical.common.ResourceNotFoundException;
import com.fitnessmedical.dto.account.AccountResponse;
import com.fitnessmedical.dto.admin.AdminMemberResponse;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.entity.MemberStatus;
import com.fitnessmedical.entity.ProfessionalType;
import com.fitnessmedical.repository.AccountRepository;

class AdminServiceTest {

    final AccountRepository accountRepository = mock(AccountRepository.class);
    final AdminService adminService = new AdminService(accountRepository);

    @Test
    @DisplayName("관리자는 전문직 목록을 조회할 수 있다.")
    void listProfessionals_admin_returnsProfessionals() {
        Account admin = adminAccount();
        Account pending = professional("trainer02", false);
        given(accountRepository.findByLoginId("admin01")).willReturn(Optional.of(admin));
        given(accountRepository.findByRoleOrderByProfessionalVerifiedAscIdAsc(AccountRole.PROFESSIONAL))
                .willReturn(List.of(pending));

        List<AccountResponse> result = adminService.listProfessionals("admin01");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().loginId()).isEqualTo("trainer02");
        assertThat(result.getFirst().professionalVerified()).isFalse();
    }

    @Test
    @DisplayName("관리자는 일반 회원 가입 현황을 조회할 수 있다.")
    void listMembers_admin_returnsMembers() {
        Member linked = mock(Member.class);
        given(linked.getId()).willReturn(1L);
        given(linked.getGender()).willReturn("여성");
        given(linked.getAge()).willReturn(56);
        given(linked.getGoal()).willReturn("건강한 일상 복귀");
        given(linked.getProgress()).willReturn(70);
        given(linked.getStatus()).willReturn(MemberStatus.GOOD);
        given(linked.getLastMeasuredDate()).willReturn(LocalDate.of(2026, 7, 19));
        Account member = new Account("member01", "encoded", "김순자", AccountRole.MEMBER, linked);
        given(accountRepository.findByLoginId("admin01")).willReturn(Optional.of(adminAccount()));
        given(accountRepository.findByRoleOrderByIdAsc(AccountRole.MEMBER))
                .willReturn(List.of(member));

        List<AdminMemberResponse> result = adminService.listMembers("admin01");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().loginId()).isEqualTo("member01");
        assertThat(result.getFirst().status()).isEqualTo("양호");
        assertThat(result.getFirst().memberId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("관리자가 아니면 회원 가입 현황을 볼 수 없다.")
    void listMembers_professional_isForbidden() {
        Account professional = professional("trainer01", true);
        given(accountRepository.findByLoginId("trainer01")).willReturn(Optional.of(professional));

        assertThatThrownBy(() -> adminService.listMembers("trainer01"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("관리자 권한이 필요합니다.");
    }

    @Test
    @DisplayName("관리자가 아니면 전문직 목록을 볼 수 없다.")
    void listProfessionals_member_isForbidden() {
        Account member = new Account("member01", "encoded", "회원", AccountRole.MEMBER, null);
        given(accountRepository.findByLoginId("member01")).willReturn(Optional.of(member));

        assertThatThrownBy(() -> adminService.listProfessionals("member01"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("관리자 권한이 필요합니다.");
    }

    @Test
    @DisplayName("관리자는 전문직 인증을 승인할 수 있다.")
    void verifyProfessional_setsVerifiedTrue() {
        Account admin = adminAccount();
        Account pending = professional("trainer02", false);
        given(accountRepository.findByLoginId("admin01")).willReturn(Optional.of(admin));
        given(accountRepository.findById(2L)).willReturn(Optional.of(pending));

        AccountResponse result = adminService.setProfessionalVerified("admin01", 2L, true);

        assertThat(pending.isProfessionalVerified()).isTrue();
        assertThat(result.professionalVerified()).isTrue();
    }

    @Test
    @DisplayName("관리자는 전문직 인증을 해제할 수 있다.")
    void revokeProfessional_setsVerifiedFalse() {
        Account admin = adminAccount();
        Account verified = professional("doctor01", true);
        given(accountRepository.findByLoginId("admin01")).willReturn(Optional.of(admin));
        given(accountRepository.findById(3L)).willReturn(Optional.of(verified));

        AccountResponse result = adminService.setProfessionalVerified("admin01", 3L, false);

        assertThat(verified.isProfessionalVerified()).isFalse();
        assertThat(result.professionalVerified()).isFalse();
    }

    @Test
    @DisplayName("없는 계정은 404로 거절한다.")
    void verifyProfessional_missingAccount_throwsNotFound() {
        given(accountRepository.findByLoginId("admin01")).willReturn(Optional.of(adminAccount()));
        given(accountRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.setProfessionalVerified("admin01", 99L, true))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("계정을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("회원 계정은 전문직 인증 대상이 아니다.")
    void verifyProfessional_memberTarget_throwsInvalidRequest() {
        Account member = new Account("member01", "encoded", "회원", AccountRole.MEMBER, null);
        given(accountRepository.findByLoginId("admin01")).willReturn(Optional.of(adminAccount()));
        given(accountRepository.findById(1L)).willReturn(Optional.of(member));

        assertThatThrownBy(() -> adminService.setProfessionalVerified("admin01", 1L, true))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("전문직 계정만 인증할 수 있습니다.");
    }

    private Account adminAccount() {
        return new Account("admin01", "encoded", "관리자", AccountRole.ADMIN, null);
    }

    private Account professional(String loginId, boolean verified) {
        return new Account(
                loginId,
                "encoded",
                "전문가",
                AccountRole.PROFESSIONAL,
                null,
                ProfessionalType.TRAINER,
                null,
                verified
        );
    }
}
