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
import com.fitnessmedical.entity.ProfessionalType;
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

    final AccountRepository accountRepository = mock(AccountRepository.class);

    final MemberRepository memberRepository = mock(MemberRepository.class);

    final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    final AccountService accountService = new AccountService(
            accountRepository,
            memberRepository,
            passwordEncoder
    );

    @Test
    @DisplayName("MEMBER 계정은 회원 연결이나 프로필이 없으면 생성할 수 없다.")
    void create_memberWithoutMemberIdOrProfile_throwsInvalidRequestException() {
        AccountCreateRequest request = memberRequest(null);

        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("MEMBER 계정은 회원 연결 또는 프로필(성별·나이·키·체중·목표)이 필요합니다.");
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("MEMBER 계정은 회원 연결이 있으면 생성할 수 있다.")
    void create_memberWithMemberId_createsAccount() {
        Member member = sampleMember();
        AccountCreateRequest request = memberRequest(1L);

        given(accountRepository.existsByLoginId("member01")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encoded-password");
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(accountRepository.existsByMember_Id(1L)).willReturn(false);
        given(accountRepository.save(any(Account.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        accountService.create(request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        Account savedAccount = captor.getValue();
        assertThat(savedAccount.getMember()).isSameAs(member);
        assertThat(savedAccount.getRole()).isEqualTo(AccountRole.MEMBER);
        assertThat(savedAccount.isProfessionalVerified()).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 memberId는 연결할 수 없다.")
    void create_memberWithUnknownMemberId_throwsResourceNotFoundException() {
        AccountCreateRequest request = memberRequest(999L);

        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("연결할 회원을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("이미 연결된 memberId는 다시 연결할 수 없다.")
    void create_memberWithAlreadyLinkedMemberId_throwsDuplicateResourceException() {
        Member member = sampleMember();
        AccountCreateRequest request = memberRequest(1L);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(accountRepository.existsByMember_Id(1L)).willReturn(true);

        assertThatThrownBy(() -> accountService.create(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessage("이미 연결된 회원입니다.");
    }

    @Test
    @DisplayName("PROFESSIONAL 계정은 memberId를 가질 수 없다.")
    void create_professionalWithMemberId_throwsInvalidRequestException() {
        AccountCreateRequest request = new AccountCreateRequest(
                "pro01",
                "password123",
                "전문가",
                AccountRole.PROFESSIONAL,
                1L,
                ProfessionalType.TRAINER,
                "SP21001234",
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("PROFESSIONAL 계정은 memberId를 가질 수 없습니다.");
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("트레이너는 자격번호 없이도 가입할 수 있다.")
    void create_trainerWithoutCertificate_isUnverified() {
        AccountCreateRequest request = new AccountCreateRequest(
                "trainer01",
                "password123",
                "김길명",
                AccountRole.PROFESSIONAL,
                null,
                ProfessionalType.TRAINER,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        given(accountRepository.existsByLoginId("trainer01")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encoded-password");
        given(accountRepository.save(any(Account.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        accountService.create(request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Account saved = captor.getValue();
        assertThat(saved.getProfessionalType()).isEqualTo(ProfessionalType.TRAINER);
        assertThat(saved.getLicenseNumber()).isNull();
        assertThat(saved.isProfessionalVerified()).isFalse();
    }

    @Test
    @DisplayName("트레이너 자격번호 형식이 아니면 가입할 수 없다.")
    void create_trainerWithInvalidCertificate_throwsInvalidRequestException() {
        AccountCreateRequest request = new AccountCreateRequest(
                "trainer01",
                "password123",
                "김길명",
                AccountRole.PROFESSIONAL,
                null,
                ProfessionalType.TRAINER,
                "123456",
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("생활스포츠지도사 자격번호 형식이 올바르지 않습니다.");
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("트레이너는 자격번호 형식이 맞으면 전문직 인증이 완료된다.")
    void create_trainerWithValidCertificate_isVerified() {
        AccountCreateRequest request = new AccountCreateRequest(
                "trainer01",
                "password123",
                "김길명",
                AccountRole.PROFESSIONAL,
                null,
                ProfessionalType.TRAINER,
                "sp21001234",
                null,
                null,
                null,
                null,
                null,
                null
        );
        given(accountRepository.existsByLoginId("trainer01")).willReturn(false);
        given(accountRepository.existsByLicenseNumber("sp21001234")).willReturn(false);
        given(accountRepository.existsByLicenseNumber("SP21001234")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encoded-password");
        given(accountRepository.save(any(Account.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        accountService.create(request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Account saved = captor.getValue();
        assertThat(saved.getProfessionalType()).isEqualTo(ProfessionalType.TRAINER);
        assertThat(saved.getLicenseNumber()).isEqualTo("SP21001234");
        assertThat(saved.isProfessionalVerified()).isTrue();
    }

    @Test
    @DisplayName("전문의는 면허번호가 없으면 가입할 수 없다.")
    void create_physicianWithoutLicense_throwsInvalidRequestException() {
        AccountCreateRequest request = new AccountCreateRequest(
                "doctor01",
                "password123",
                "홍길동",
                AccountRole.PROFESSIONAL,
                null,
                ProfessionalType.PHYSICIAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("전문의는 면허번호가 필요합니다.");
    }

    @Test
    @DisplayName("전문의 면허번호 형식이 아니면 가입할 수 없다.")
    void create_physicianWithInvalidLicense_throwsInvalidRequestException() {
        AccountCreateRequest request = new AccountCreateRequest(
                "doctor01",
                "password123",
                "홍길동",
                AccountRole.PROFESSIONAL,
                null,
                ProfessionalType.PHYSICIAN,
                "ABC",
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("전문의 면허번호 형식이 올바르지 않습니다.");
    }

    @Test
    @SuppressWarnings("null")
    @DisplayName("전문의는 면허번호 형식이 맞으면 전문직 인증이 완료된다.")
    void create_physicianWithValidLicense_isVerified() {
        AccountCreateRequest request = new AccountCreateRequest(
                "doctor01",
                "password123",
                "홍길동",
                AccountRole.PROFESSIONAL,
                null,
                ProfessionalType.PHYSICIAN,
                "123456",
                null,
                null,
                null,
                null,
                null,
                null
        );
        given(accountRepository.existsByLoginId("doctor01")).willReturn(false);
        given(accountRepository.existsByLicenseNumber("123456")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encoded-password");
        given(accountRepository.save(any(Account.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        accountService.create(request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Account saved = captor.getValue();
        assertThat(saved.getProfessionalType()).isEqualTo(ProfessionalType.PHYSICIAN);
        assertThat(saved.getLicenseNumber()).isEqualTo("123456");
        assertThat(saved.isProfessionalVerified()).isTrue();
    }

    private AccountCreateRequest memberRequest(Long memberId) {
        return new AccountCreateRequest(
                "member01",
                "password123",
                "회원",
                AccountRole.MEMBER,
                memberId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private Member sampleMember() {
        return new Member(
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
    }
}
