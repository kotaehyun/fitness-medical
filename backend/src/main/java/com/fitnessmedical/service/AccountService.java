package com.fitnessmedical.service;


import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.fitnessmedical.common.DuplicateResourceException;
import com.fitnessmedical.common.InvalidCredentialsException;
import com.fitnessmedical.common.InvalidRequestException;
import com.fitnessmedical.dto.account.AccountCreateRequest;
import com.fitnessmedical.dto.account.AccountLoginRequest;
import com.fitnessmedical.dto.account.AccountResponse;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.entity.MemberStatus;
import com.fitnessmedical.entity.ProfessionalType;
import com.fitnessmedical.repository.AccountRepository;
import com.fitnessmedical.repository.MemberRepository;

/**
 * [공부/면접] 계정(로그인) 생성·조회·인증 검증을 담당하는 Service 계층입니다.
 *
 * Q. Controller가 Repository를 직접 호출하지 않고 Service를 거치는 이유는?
 * A. 비즈니스 규칙(역할별 member 연결, 중복 검사, 비밀번호 해싱)을 한곳에 모아
 *    Controller는 HTTP 변환, Repository는 DB 접근만 담당하는 계층형 구조를 유지하기 위해서입니다.
 *
 * Q. 클래스에 @Transactional(readOnly = true)를 붙이는 이유는?
 * A. 기본 조회 메서드(login, findResponseByLoginId)는 DB를 변경하지 않으므로
 *    읽기 전용 트랜잭션으로 성능·안전성을 확보하고, 쓰기가 필요한 create()만
 *    메서드 단위 @Transactional로 덮어씁니다.
 *
 * Q. 이 Service에서 던지는 예외와 HTTP 상태 코드는?
 * A. InvalidRequestException → 400,
 *    DuplicateResourceException → 409, InvalidCredentialsException → 401
 *    (GlobalExceptionHandler가 매핑합니다.)
 *
 * Q. 전문직 인증은 실제 조회인가?
 * A. 아니다. 학습용 형식 확인이다. 전문의 면허·생활스포츠지도사 자격번호를
 *    국가 시스템에 조회하지 않으며, 진단·처방 권한도 주지 않는다.
 *
 * Q. [하드코딩] region은?
 * A. 직접 구현한 핵심 규칙을 IDE에서 접을 수 있게 표시한 학습용 구간이다.
 */
@Service
@Transactional(readOnly = true)
public class AccountService {

    private static final Pattern PHYSICIAN_LICENSE = Pattern.compile("^[0-9]{5,10}$");
    // 학습용: 생활스포츠지도사 자격번호 — 영문 2자 + 숫자 6~12자 (예: SP21001234)
    private static final Pattern TRAINER_CERTIFICATE = Pattern.compile("^[A-Za-z]{2}[0-9]{6,12}$");

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(
            AccountRepository accountRepository,
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * [공부/면접] 회원가입 — 계정을 DB에 저장합니다.
     *
     * 흐름: loginId 중복 검사 → 전문직 인증 규칙 → PasswordEncoder 해싱 → resolveMember
     *       → Account 저장 → AccountResponse 반환
     *
     * 면접 포인트: PasswordEncoder.encode()는 단방향 해시입니다.
     *             평문 password·면허번호를 로그·응답·DB 평문에 넣으면 안 됩니다.
     */
    @Transactional
    public AccountResponse create(AccountCreateRequest request) {
        // region [하드코딩] 회원가입 중복 검사·저장
        // exists와 save 사이 레이스는 uk_accounts_login_id + DataIntegrityViolationException(409)이 막는다.
        if (accountRepository.existsByLoginId(request.loginId())) {
            throw new DuplicateResourceException("이미 사용 중인 로그인 아이디입니다.");
        }

        ProfessionalCredentials credentials = resolveProfessionalCredentials(request);
        String encodedPassword = passwordEncoder.encode(request.password());
        Member member = resolveMember(request);

        Account account = new Account(
                request.loginId(),
                encodedPassword,
                request.displayName(),
                request.role(),
                member,
                credentials.type(),
                credentials.licenseNumber(),
                credentials.verified()
        );

        Account saved = accountRepository.save(account);
        return AccountResponse.from(saved);
        // endregion
    }

    /**
     * [공부/면접] 전문가 유형·자격/면허 인증 규칙
     *
     * - MEMBER → professionalType/자격·면허번호 금지
     * - PROFESSIONAL → professionalType 필수
     * - TRAINER → 생활스포츠지도사 자격번호는 우대(선택). 없으면 저장만, 있으면 형식 검사
     * - PHYSICIAN → 면허번호(숫자 5~10자리) 필수, 형식만 검사
     * - 공개 가입은 형식이 맞아도 verified=false. 관리자 승인(시드 계정) 전까지 전문가 API 불가
     */
    private ProfessionalCredentials resolveProfessionalCredentials(AccountCreateRequest request) {
        // region [하드코딩] 전문직 유형·자격/면허 규칙
        String license = blankToNull(request.licenseNumber());

        if (request.role() == AccountRole.MEMBER) {
            if (request.professionalType() != null || license != null) {
                throw new InvalidRequestException("회원 계정은 전문직 유형·자격/면허번호를 가질 수 없습니다.");
            }
            return new ProfessionalCredentials(null, null, false);
        }

        if (request.role() != AccountRole.PROFESSIONAL) {
            throw new InvalidRequestException("지원하지 않는 계정 역할입니다.");
        }

        if (request.professionalType() == null) {
            throw new InvalidRequestException("전문가 유형(트레이너/전문의)이 필요합니다.");
        }

        if (request.professionalType() == ProfessionalType.TRAINER) {
            // 자격번호는 우대(선택). 없어도 가입되고, 있으면 형식만 검사한다.
            if (license == null) {
                return new ProfessionalCredentials(ProfessionalType.TRAINER, null, false);
            }
            if (!TRAINER_CERTIFICATE.matcher(license).matches()) {
                throw new InvalidRequestException("생활스포츠지도사 자격번호 형식이 올바르지 않습니다.");
            }
            ensureUniqueCredential(license);
            return new ProfessionalCredentials(ProfessionalType.TRAINER, license.toUpperCase(), false);
        }

        if (license == null) {
            throw new InvalidRequestException("전문의는 면허번호가 필요합니다.");
        }
        if (!PHYSICIAN_LICENSE.matcher(license).matches()) {
            throw new InvalidRequestException("전문의 면허번호 형식이 올바르지 않습니다.");
        }
        ensureUniqueCredential(license);
        return new ProfessionalCredentials(ProfessionalType.PHYSICIAN, license, false);
        // endregion
    }

    private void ensureUniqueCredential(String license) {
        // region [하드코딩] 자격/면허번호 중복
        if (accountRepository.existsByLicenseNumber(license)
                || accountRepository.existsByLicenseNumber(license.toUpperCase())) {
            throw new DuplicateResourceException("이미 등록된 자격/면허번호입니다.");
        }
        // endregion
    }

    /**
     * [공부/면접] 공개 가입의 Member 연결 규칙
     *
     * - memberId가 오면 거절한다. 공개 API로 기존 회원을 가로채면 안 된다.
     * - MEMBER → 프로필로 새 Member를 만든다.
     * - PROFESSIONAL → Member를 연결하지 않는다.
     * - 기존 회원 연결은 관리자·초대 API(미구현)에서만 한다.
     */
    private Member resolveMember(AccountCreateRequest request) {
        // region [하드코딩] MEMBER/PROFESSIONAL 회원 연결 규칙
        if (request.memberId() != null) {
            throw new InvalidRequestException("공개 가입에서는 기존 회원을 연결할 수 없습니다.");
        }

        if (request.role() == AccountRole.MEMBER) {
            return createMemberProfile(request);
        }

        return null;
        // endregion
    }

    private Member createMemberProfile(AccountCreateRequest request) {
        // region [하드코딩] MEMBER 프로필로 Member 생성
        if (isBlank(request.gender()) || request.age() == null
                || request.height() == null || request.weight() == null
                || isBlank(request.goal())) {
            throw new InvalidRequestException("MEMBER 계정은 프로필(성별·나이·키·체중·목표)이 필요합니다.");
        }

        int progress = request.progress() == null ? 0 : request.progress();
        Member member = new Member(
                request.displayName(),
                request.gender().trim(),
                request.age(),
                request.height(),
                request.weight(),
                request.goal().trim(),
                progress,
                MemberStatus.CHECK_REQUIRED,
                null
        );
        return memberRepository.save(member);
        // endregion
    }

    /**
     * [공부/면접] loginId·비밀번호 일치 여부를 Service에서 직접 검증합니다.
     *
     * 흐름: loginId로 Account 조회 → 없으면 InvalidCredentialsException(401)
     *       → passwordEncoder.matches(평문, DB해시) → 불일치 시 401
     *
     * 면접 포인트: matches()는 encode()와 달리 평문과 해시를 비교합니다.
     *             "아이디 없음"과 "비밀번호 틀림"에 같은 메시지를 쓰면
     *             공격자가 유효한 loginId를 추측하기 어렵습니다(정보 노출 최소화).
     *             AuthController는 AuthenticationManager 경로를 쓰지만,
     *             이 메서드는 Service 단독 검증 패턴 학습용으로 남아 있습니다.
     */
    public AccountResponse login(AccountLoginRequest request) {
        // region [하드코딩] loginId·비밀번호 검증
        Account account = accountRepository
                .findByLoginId(request.loginId())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "아이디 또는 비밀번호가 올바르지 않습니다."
                ));
        if(!passwordEncoder.matches(
                request.password(),
                account.getPassword()
        )) {
            throw new InvalidCredentialsException(
                    "아이디 또는 비밀번호가 올바르지 않습니다."
            );
        }
        return AccountResponse.from(account);
        // endregion
    }

    /**
     * [공부/면접] loginId로 계정 정보를 조회해 응답 DTO로 변환합니다.
     *
     * 로그인 성공 후 AuthController.login()·/me 엔드포인트에서 호출됩니다.
     * 계정이 없으면 InvalidCredentialsException(401) — 비밀번호 필드는 응답에 포함하지 않습니다.
     */
    public AccountResponse findResponseByLoginId(String loginId) {
        Account account = accountRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "아이디 또는 비밀번호가 올바르지 않습니다."
                ));

        return AccountResponse.from(account);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private record ProfessionalCredentials(
            ProfessionalType type,
            String licenseNumber,
            boolean verified
    ) {
    }
}
