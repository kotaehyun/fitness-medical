package com.fitnessmedical.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitnessmedical.common.ForbiddenException;
import com.fitnessmedical.common.InvalidCredentialsException;
import com.fitnessmedical.common.InvalidRequestException;
import com.fitnessmedical.common.ResourceNotFoundException;
import com.fitnessmedical.dto.account.AccountResponse;
import com.fitnessmedical.dto.admin.AdminMemberResponse;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.repository.AccountRepository;

/**
 * [공부/면접] 관리자(ADMIN) — 가입 현황 + 전문직 인증 승인/해제
 *
 * Q. 왜 공개 가입 PROFESSIONAL은 verified=false인가?
 * A. 면허 형식만 맞다고 전문가 API를 주면 안 된다. ADMIN이 승인해야 한다.
 *
 * Q. SecurityConfig.hasRole("ADMIN")만으로 부족한 이유?
 * A. URL 권한과 별도로, 대상 계정이 PROFESSIONAL인지·존재하는지를 Service에서 검사한다.
 *
 * Q. licenseNumber를 응답에 넣나?
 * A. 넣지 않는다. AccountResponse는 인증 여부만 내려준다.
 */
@Service
@Transactional(readOnly = true)
public class AdminService {

    private final AccountRepository accountRepository;

    public AdminService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountResponse> listProfessionals(String loginId) {
        // region [핵심로직] 관리자만 전문직 목록 조회
        requireAdmin(loginId);
        return accountRepository
                .findByRoleOrderByProfessionalVerifiedAscIdAsc(AccountRole.PROFESSIONAL)
                .stream()
                .map(AccountResponse::from)
                .toList();
        // endregion
    }

    public List<AdminMemberResponse> listMembers(String loginId) {
        // region [핵심로직] 관리자만 일반 회원 가입 현황 조회
        requireAdmin(loginId);
        return accountRepository
                .findByRoleOrderByIdAsc(AccountRole.MEMBER)
                .stream()
                .map(AdminMemberResponse::from)
                .toList();
        // endregion
    }

    @Transactional
    public AccountResponse setProfessionalVerified(String loginId, Long accountId, boolean verified) {
        // region [핵심로직] 관리자가 전문직 인증 승인/해제
        requireAdmin(loginId);
        Account target = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("계정을 찾을 수 없습니다."));
        if (target.getRole() != AccountRole.PROFESSIONAL) {
            throw new InvalidRequestException("전문직 계정만 인증할 수 있습니다.");
        }
        target.setProfessionalVerified(verified);
        return AccountResponse.from(target);
        // endregion
    }

    private Account requireAdmin(String loginId) {
        // region [하드코딩] ADMIN 역할 검사
        if (loginId == null || loginId.isBlank()) {
            throw new InvalidCredentialsException("로그인이 필요합니다.");
        }
        Account account = accountRepository.findByLoginId(loginId)
                .orElseThrow(() -> new InvalidCredentialsException("로그인이 필요합니다."));
        if (account.getRole() != AccountRole.ADMIN) {
            throw new ForbiddenException("관리자 권한이 필요합니다.");
        }
        return account;
        // endregion
    }
}
