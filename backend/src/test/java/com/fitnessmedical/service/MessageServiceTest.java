package com.fitnessmedical.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fitnessmedical.common.ForbiddenException;
import com.fitnessmedical.dto.chat.ChatMessageCreateRequest;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.entity.MemberAssignment;
import com.fitnessmedical.entity.ProfessionalType;
import com.fitnessmedical.repository.AccountRepository;
import com.fitnessmedical.repository.ChatMessageRepository;
import com.fitnessmedical.repository.MemberAssignmentRepository;

class MessageServiceTest {

    final MemberAuthorizationService authorizationService = mock(MemberAuthorizationService.class);
    final AccountRepository accountRepository = mock(AccountRepository.class);
    final MemberAssignmentRepository assignmentRepository = mock(MemberAssignmentRepository.class);
    final ChatMessageRepository chatMessageRepository = mock(ChatMessageRepository.class);
    final MessageService messageService = new MessageService(
            authorizationService,
            accountRepository,
            assignmentRepository,
            chatMessageRepository
    );

    @Test
    @DisplayName("담당이 아닌 전문가에게는 메시지를 보낼 수 없다.")
    void send_unassignedProfessional_throwsForbidden() {
        Account memberAccount = memberAccount(1L, 10L);
        Account otherDoctor = physician(9L);
        given(authorizationService.requireAccount("member01")).willReturn(memberAccount);
        given(accountRepository.findById(9L)).willReturn(Optional.of(otherDoctor));
        given(assignmentRepository.findByMember_Id(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.send(
                "member01",
                new ChatMessageCreateRequest(9L, "안녕하세요")
        )).isInstanceOf(ForbiddenException.class);

        verify(chatMessageRepository, never()).save(any());
    }

    @Test
    @DisplayName("담당 전문의에게는 텍스트 메시지를 보낼 수 있다.")
    void send_assignedPhysician_savesMessage() {
        Account memberAccount = memberAccount(1L, 10L);
        Account doctor = physician(3L);
        MemberAssignment assignment = new MemberAssignment(memberAccount.getMember(), doctor, null);
        given(authorizationService.requireAccount("member01")).willReturn(memberAccount);
        given(accountRepository.findById(3L)).willReturn(Optional.of(doctor));
        given(assignmentRepository.findByMember_Id(10L)).willReturn(Optional.of(assignment));
        given(chatMessageRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        messageService.send("member01", new ChatMessageCreateRequest(3L, "걷기 일정 문의합니다"));

        verify(chatMessageRepository).save(any());
    }

    private static Account memberAccount(Long accountId, Long memberId) {
        Member member = mock(Member.class);
        given(member.getId()).willReturn(memberId);
        Account account = mock(Account.class);
        given(account.getId()).willReturn(accountId);
        given(account.getRole()).willReturn(AccountRole.MEMBER);
        given(account.getMember()).willReturn(member);
        given(account.getDisplayName()).willReturn("김순자");
        return account;
    }

    private static Account physician(Long accountId) {
        Account account = mock(Account.class);
        given(account.getId()).willReturn(accountId);
        given(account.getRole()).willReturn(AccountRole.PROFESSIONAL);
        given(account.getProfessionalType()).willReturn(ProfessionalType.PHYSICIAN);
        given(account.isProfessionalVerified()).willReturn(true);
        given(account.getDisplayName()).willReturn("홍길동");
        return account;
    }
}
