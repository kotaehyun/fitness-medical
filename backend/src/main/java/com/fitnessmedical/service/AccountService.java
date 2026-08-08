package com.fitnessmedical.service;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.fitnessmedical.common.DuplicateResourceException;
import com.fitnessmedical.common.InvalidCredentialsException;
import com.fitnessmedical.common.InvalidRequestException;
import com.fitnessmedical.common.ResourceNotFoundException;
import com.fitnessmedical.dto.account.AccountCreateRequest;
import com.fitnessmedical.dto.account.AccountLoginRequest;
import com.fitnessmedical.dto.account.AccountResponse;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.entity.Member;
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
 * A. InvalidRequestException → 400, ResourceNotFoundException → 404,
 *    DuplicateResourceException → 409, InvalidCredentialsException → 401
 *    (GlobalExceptionHandler가 매핑합니다.)
 */
@Service
@Transactional(readOnly = true)
public class AccountService {

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
     * 흐름: loginId 중복 검사 → PasswordEncoder로 평문 비밀번호 해싱 → resolveMember로
     *       역할별 Member 연결 규칙 검증 → Account Entity 저장 → AccountResponse 반환
     *
     * 면접 포인트: PasswordEncoder.encode()는 단방향 해시입니다.
     *             평문 password를 로그·응답·DB에 그대로 저장하면 안 됩니다.
     *             @Transactional(쓰기)로 INSERT가 하나의 트랜잭션으로 커밋됩니다.
     */
    @Transactional
    public AccountResponse create(AccountCreateRequest request) {

        if (accountRepository.existsByLoginId(request.loginId())) {
            throw new DuplicateResourceException("이미 사용 중인 로그인 아이디입니다.");
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());
        Member member = resolveMember(request);

        Account account = new Account(
                request.loginId(),
                encodedPassword,
                request.displayName(),
                request.role(),
                member
        );

        Account saved = accountRepository.save(account);
        return AccountResponse.from(saved);
    }

    /**
     * [공부/면접] 역할(AccountRole)에 따라 Member Entity를 연결하거나 null을 반환합니다.
     *
     * 규칙:
     * - MEMBER 역할 → memberId 필수, 없으면 InvalidRequestException(400)
     * - PROFESSIONAL 역할 → memberId 금지, 있으면 InvalidRequestException(400)
     * - memberId가 있으면 → Member 존재 확인(없으면 ResourceNotFoundException 404)
     * - 이미 다른 Account에 연결된 Member → DuplicateResourceException(409)
     * - memberId가 null이고 위 제약에 해당 없으면 → null 반환(PROFESSIONAL 등)
     *
     * 면접 포인트: private 메서드로 도메인 규칙을 분리하면 create() 흐름이 읽기 쉽고
     *             테스트·재사용도 용이합니다.
     */
    private Member resolveMember(AccountCreateRequest request) {

        Long memberId = request.memberId();

        if (request.role() == AccountRole.MEMBER && memberId == null) {
            throw new InvalidRequestException(
                        "MEMBER 계정은 회원 연결이 필요합니다."
            );
        }

        if (request.role() == AccountRole.PROFESSIONAL && memberId != null) {
            throw new InvalidRequestException(
                        "PROFESSIONAL 계정은 memberId를 가질 수 없습니다."
            );
        }

        if (memberId == null) {
            return null;
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "연결할 회원을 찾을 수 없습니다."
                ));

        if (accountRepository.existsByMember_Id(memberId)) {
            throw new DuplicateResourceException(
                    "이미 연결된 회원입니다."
            );
        }

        return member;
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
}
