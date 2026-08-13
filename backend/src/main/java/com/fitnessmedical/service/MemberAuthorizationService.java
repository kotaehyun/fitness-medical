package com.fitnessmedical.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitnessmedical.common.ForbiddenException;
import com.fitnessmedical.common.InvalidCredentialsException;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.entity.ProfessionalType;
import com.fitnessmedical.repository.AccountRepository;

/**
 * [공부/면접] 회원 API 소유권·역할 검사
 *
 * Q. SecurityConfig.authenticated()만으로 부족한 이유?
 * A. 로그인한 것만으로는 “누구의 memberId인지”를 막지 못한다.
 *    MEMBER는 연결된 회원만, PROFESSIONAL은 전체 회원을 본다.
 *
 * Q. 왜 loginId로 Account를 다시 읽나?
 * A. UserDetails에는 username(loginId)·role만 있다. memberId·displayName은 Account에 있다.
 *
 * Q. professionalVerified를 여기서 막으면?
 * A. 전문의는 면허 인증이 필수라서 verified=false면 403.
 *    트레이너 자격은 우대라서 verified=false여도 PROFESSIONAL이면 피드백·회원 CRUD 가능.
 */
@Service
@Transactional(readOnly = true)
public class MemberAuthorizationService {

    private final AccountRepository accountRepository;

    public MemberAuthorizationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account requireAccount(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new InvalidCredentialsException("로그인이 필요합니다.");
        }
        return accountRepository.findByLoginId(loginId)
                .orElseThrow(() -> new InvalidCredentialsException("로그인이 필요합니다."));
    }

    public Account requireProfessional(String loginId) {
        Account account = requireAccount(loginId);
        if (account.getRole() != AccountRole.PROFESSIONAL) {
            throw new ForbiddenException("전문가 권한이 필요합니다.");
        }
        boolean unverifiedTrainer = account.getProfessionalType() == ProfessionalType.TRAINER;
        if (!account.isProfessionalVerified() && !unverifiedTrainer) {
            throw new ForbiddenException("전문직 인증이 완료되지 않았습니다.");
        }
        return account;
    }

    public void assertCanAccessMember(String loginId, Long memberId) {
        Account account = requireAccount(loginId);
        if (account.getRole() == AccountRole.PROFESSIONAL) {
            return;
        }
        if (account.getRole() != AccountRole.MEMBER) {
            throw new ForbiddenException("해당 회원 데이터에 접근할 수 없습니다.");
        }
        Member linked = account.getMember();
        if (linked == null || linked.getId() == null || !linked.getId().equals(memberId)) {
            throw new ForbiddenException("해당 회원 데이터에 접근할 수 없습니다.");
        }
    }
}
