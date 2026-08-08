package com.fitnessmedical.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;


import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.entity.MemberStatus;
import com.fitnessmedical.common.DuplicateResourceException;
import com.fitnessmedical.common.InvalidRequestException;
import com.fitnessmedical.common.ResourceNotFoundException;
import com.fitnessmedical.dto.account.AccountCreateRequest;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.repository.AccountRepository;
import com.fitnessmedical.repository.MemberRepository;

/**
 * [공부/면접] AccountService 단위 테스트
 *
 * Q. 왜 Controller 테스트가 아니라 Service 단위 테스트인가?
 * A. 역할·회원 연결 규칙은 비즈니스 로직이다. DB/HTTP 없이 Service만 빠르게 검증한다.
 *
 * Q. mock을 쓰는 이유는?
 * A. Repository·PasswordEncoder는 외부 의존성이다.
 *    실제 DB/암호화 대신 "정해진 응답"을 주어 분기만 검증한다.
 *
 * Q. 예외 타입을 왜 중요하게 보나?
 * A. GlobalExceptionHandler가 예외 타입 → HTTP 상태로 매핑한다.
 *    InvalidRequestException → 400, ResourceNotFoundException → 404,
 *    DuplicateResourceException → 409
 */
class AccountServiceTest {

    // Mockito.mock(): Spring 컨텍스트 없이 가짜 객체를 만든다. (순수 단위 테스트)
    final AccountRepository accountRepository = mock(AccountRepository.class);

    final MemberRepository memberRepository = mock(MemberRepository.class);

    final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    // 생성자 주입으로 실제 AccountService를 만든다. 의존성만 mock이다.
    final AccountService accountService = new AccountService(
            accountRepository,
            memberRepository,
            passwordEncoder
    );

    /**
     * [케이스] MEMBER + memberId null
     * - 검증 레이어(@Valid)가 아니라 Service 규칙이다.
     * - memberId는 DTO에서 nullable이다. (PROFESSIONAL은 null 허용)
     * - 그래서 "MEMBER면 필수" 검사는 resolveMember()에서 한다.
     */
    @Test
    @DisplayName("MEMBER 계정은 회원 연결이 없으면 생성할 수 없다.")
    void create_memberWithoutMemberId_throwsInvalidRequestException() {
        AccountCreateRequest request = new AccountCreateRequest(
                "member01",
                "password123",
                "회원",
                AccountRole.MEMBER,
                null
        );

        // assertThatThrownBy: 예외가 발생하는지 + 타입/메시지를 한 번에 검증
        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("MEMBER 계정은 회원 연결이 필요합니다.");
    }

    /**
     * [케이스] MEMBER + 정상 memberId → 저장 성공
     * - given(...): BDD 스타일 stub. "이런 입력이면 이렇게 응답"을 미리 정의
     * - ArgumentCaptor: save()에 넘어간 실제 인자를 꺼내 검증할 때 사용
     * - isSameAs: 동일 참조인지 확인. (같은 Member 객체가 연결됐는지)
     */
    @Test
    @SuppressWarnings("null")
    @DisplayName("MEMBER 계정은 회원 연결이 있으면 생성할 수 있다.")
    void create_memberWithMemberId_createsAccount() {
        Member member = new Member(
               "홍길동",
               "Male",
               35,
               175.5,
               72.3,
               "건강 습관 만들기",
               10,
               MemberStatus.GOOD,
               LocalDate.now()
        );

        AccountCreateRequest request = new AccountCreateRequest(
            "member01",
            "password123",
            "홍길동",
            AccountRole.MEMBER,
            1L
        );

        // 성공 경로의 사전조건: 로그인ID 미중복, 회원 존재, 아직 미연결
        given(accountRepository.existsByLoginId("member01")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encoded-password");
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(accountRepository.existsByMember_Id(1L)).willReturn(false);
        // save 인자를 그대로 반환 → 저장된 엔티티를 이어서 검증하기 쉽도록
        given(accountRepository.save(any(Account.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        accountService.create(request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        // verify: save가 정말 호출됐는지 + 그때의 인자를 captor에 담는다
        verify(accountRepository).save(captor.capture());

        Account savedAccount = captor.getValue();
        assertThat(savedAccount.getMember()).isSameAs(member);
        assertThat(savedAccount.getRole()).isEqualTo(AccountRole.MEMBER);
    }

    /**
     * [케이스] 존재하지 않는 memberId
     * - Optional.empty() → orElseThrow → ResourceNotFoundException
     * - 면접 포인트: "없는 리소스"는 404가 자연스럽다. (Handler에서 매핑)
     * - existsByMember_Id는 호출 전에 끝나므로 stub 불필요
     */
    @Test
    @DisplayName("존재하지 않는 memberId는 연결할 수 없다.")
    void create_memberWithUnknownMemberId_throwsResourceNotFoundException() {
        AccountCreateRequest request = new AccountCreateRequest(
                "member01",
                "password123",
                "회원",
                AccountRole.MEMBER,
                999L
        );

        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("연결할 회원을 찾을 수 없습니다.");
    }

    /**
     * [케이스] 이미 연결된 memberId
     * - 회원은 존재(findById 성공)하지만 existsByMember_Id == true
     * - 1:1 관계이므로 중복 연결은 Conflict(409)로 표현하는 편이 맞다
     * - 순서 중요: "존재 여부" 확인 후 "이미 연결됐는지" 확인
     */
    @Test
    @DisplayName("이미 연결된 memberId는 다시 연결할 수 없다.")
    void create_memberWithAlreadyLinkedMemberId_throwsDuplicateResourceException() {
        Member member = new Member(
            "홍길동",
            "Male",
            35,
            175.5,
            72.3,
            "건강 습관 만들기",
            10,
            MemberStatus.GOOD,
            LocalDate.now()
        );

        AccountCreateRequest request = new AccountCreateRequest(
            "member01",
            "password123",
            "회원",
            AccountRole.MEMBER,
            1L
        );

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(accountRepository.existsByMember_Id(1L)).willReturn(true);

        assertThatThrownBy(() -> accountService.create(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessage("이미 연결된 회원입니다.");
    }

    /**
     * [케이스] PROFESSIONAL + memberId 제공
     * - 역할별 불변식(invariant): PROFESSIONAL은 Member와 연결하지 않는다
     * - findById 전에 막히므로 Repository stub이 필요 없다
     * - 잘못된 요청 → InvalidRequestException → HTTP 400
     */
    @Test
    @DisplayName("PROFESSIONAL 계정은 memberId를 가질 수 없다.")
    void create_professionalWithMemberId_throwsInvalidRequestException() {
        AccountCreateRequest request = new AccountCreateRequest(
                "pro01",
                "password123",
                "전문가",
                AccountRole.PROFESSIONAL,
                1L
        );

        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("PROFESSIONAL 계정은 memberId를 가질 수 없습니다.");
    }


}
