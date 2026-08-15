package com.fitnessmedical.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitnessmedical.common.ForbiddenException;
import com.fitnessmedical.common.InvalidRequestException;
import com.fitnessmedical.common.ResourceNotFoundException;
import com.fitnessmedical.dto.chat.ChatMessageCreateRequest;
import com.fitnessmedical.dto.chat.ChatMessageResponse;
import com.fitnessmedical.dto.chat.ChatPeerResponse;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.entity.ChatMessage;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.entity.MemberAssignment;
import com.fitnessmedical.entity.ProfessionalType;
import com.fitnessmedical.repository.AccountRepository;
import com.fitnessmedical.repository.ChatMessageRepository;
import com.fitnessmedical.repository.MemberAssignmentRepository;

/**
 * [공부/면접] 텍스트 1:1. 담당 연결된 상대만.
 *
 * Q. 인증된 전문가 전원과 대화하면?
 * A. 담당이 아닌 회원 건강 맥락까지 열린다. 목록은 전체를 보되 채팅은 담당 선만.
 *
 * Q. 진단·협진인가?
 * A. 아니다. 짧은 텍스트 전달만.
 */
@Service
@Transactional(readOnly = true)
public class MessageService {

    private final MemberAuthorizationService memberAuthorizationService;
    private final AccountRepository accountRepository;
    private final MemberAssignmentRepository memberAssignmentRepository;
    private final ChatMessageRepository chatMessageRepository;

    public MessageService(
            MemberAuthorizationService memberAuthorizationService,
            AccountRepository accountRepository,
            MemberAssignmentRepository memberAssignmentRepository,
            ChatMessageRepository chatMessageRepository
    ) {
        this.memberAuthorizationService = memberAuthorizationService;
        this.accountRepository = accountRepository;
        this.memberAssignmentRepository = memberAssignmentRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<ChatPeerResponse> listPeers(String loginId) {
        Account me = memberAuthorizationService.requireAccount(loginId);
        if (me.getRole() == AccountRole.ADMIN) {
            throw new ForbiddenException("관리자는 메시지를 사용할 수 없습니다.");
        }
        if (me.getRole() == AccountRole.PROFESSIONAL && !me.isProfessionalVerified()) {
            return List.of();
        }

        Map<Long, ChatPeerResponse> peers = new LinkedHashMap<>();
        if (me.getRole() == AccountRole.MEMBER) {
            addMemberAssignedPeers(me, peers);
        } else if (me.getRole() == AccountRole.PROFESSIONAL) {
            addProfessionalPeers(me, peers);
        }
        return List.copyOf(peers.values());
    }

    public List<ChatMessageResponse> listMessages(String loginId, Long peerAccountId) {
        Account me = memberAuthorizationService.requireAccount(loginId);
        Account peer = getAccount(peerAccountId);
        assertCanChat(me, peer);
        return chatMessageRepository.findConversation(me.getId(), peer.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ChatMessageResponse send(String loginId, ChatMessageCreateRequest request) {
        Account me = memberAuthorizationService.requireAccount(loginId);
        Account peer = getAccount(request.peerAccountId());
        assertCanChat(me, peer);
        ChatMessage saved = chatMessageRepository.save(
                new ChatMessage(me, peer, request.body().trim(), LocalDateTime.now())
        );
        return toResponse(saved);
    }

    void assertCanChat(Account a, Account b) {
        if (a.getId() == null || b.getId() == null || a.getId().equals(b.getId())) {
            throw new InvalidRequestException("나와의 대화는 보낼 수 없습니다.");
        }
        if (a.getRole() == AccountRole.ADMIN || b.getRole() == AccountRole.ADMIN) {
            throw new ForbiddenException("관리자와는 대화할 수 없습니다.");
        }
        if (!isAllowedPair(a, b)) {
            throw new ForbiddenException("담당으로 연결된 상대와만 대화할 수 있습니다.");
        }
    }

    private boolean isAllowedPair(Account a, Account b) {
        if (isMemberProfessionalPair(a, b)) {
            Account memberAccount = a.getRole() == AccountRole.MEMBER ? a : b;
            Account professional = a.getRole() == AccountRole.PROFESSIONAL ? a : b;
            return isAssignedProfessional(memberAccount, professional);
        }
        if (a.getRole() == AccountRole.PROFESSIONAL && b.getRole() == AccountRole.PROFESSIONAL) {
            return shareAssignedMember(a, b);
        }
        return false;
    }

    private boolean isMemberProfessionalPair(Account a, Account b) {
        return (a.getRole() == AccountRole.MEMBER && b.getRole() == AccountRole.PROFESSIONAL)
                || (b.getRole() == AccountRole.MEMBER && a.getRole() == AccountRole.PROFESSIONAL);
    }

    private boolean isAssignedProfessional(Account memberAccount, Account professional) {
        if (!professional.isProfessionalVerified()) {
            return false;
        }
        Member member = memberAccount.getMember();
        if (member == null || member.getId() == null) {
            return false;
        }
        return memberAssignmentRepository.findByMember_Id(member.getId())
                .map(assignment -> matchesAssignment(assignment, professional))
                .orElse(false);
    }

    private boolean matchesAssignment(MemberAssignment assignment, Account professional) {
        if (professional.getProfessionalType() == ProfessionalType.PHYSICIAN) {
            return sameAccount(assignment.getPhysician(), professional);
        }
        if (professional.getProfessionalType() == ProfessionalType.TRAINER) {
            return sameAccount(assignment.getTrainer(), professional);
        }
        return false;
    }

    private boolean shareAssignedMember(Account a, Account b) {
        if (!a.isProfessionalVerified() || !b.isProfessionalVerified()) {
            return false;
        }
        Long physicianId = physicianId(a, b);
        Long trainerId = trainerId(a, b);
        if (physicianId == null || trainerId == null) {
            return false;
        }
        return memberAssignmentRepository.existsByPhysician_IdAndTrainer_Id(physicianId, trainerId);
    }

    private Long physicianId(Account a, Account b) {
        if (a.getProfessionalType() == ProfessionalType.PHYSICIAN) {
            return a.getId();
        }
        if (b.getProfessionalType() == ProfessionalType.PHYSICIAN) {
            return b.getId();
        }
        return null;
    }

    private Long trainerId(Account a, Account b) {
        if (a.getProfessionalType() == ProfessionalType.TRAINER) {
            return a.getId();
        }
        if (b.getProfessionalType() == ProfessionalType.TRAINER) {
            return b.getId();
        }
        return null;
    }

    private void addMemberAssignedPeers(Account memberAccount, Map<Long, ChatPeerResponse> peers) {
        Member member = memberAccount.getMember();
        if (member == null || member.getId() == null) {
            return;
        }
        memberAssignmentRepository.findByMember_Id(member.getId()).ifPresent(assignment -> {
            putPeer(peers, assignment.getPhysician(), "담당 전문의");
            putPeer(peers, assignment.getTrainer(), "담당 트레이너");
        });
    }

    private void addProfessionalPeers(Account professional, Map<Long, ChatPeerResponse> peers) {
        List<MemberAssignment> assignments = new ArrayList<>();
        if (professional.getProfessionalType() == ProfessionalType.PHYSICIAN) {
            assignments.addAll(memberAssignmentRepository.findByPhysician_Id(professional.getId()));
        } else if (professional.getProfessionalType() == ProfessionalType.TRAINER) {
            assignments.addAll(memberAssignmentRepository.findByTrainer_Id(professional.getId()));
        }
        for (MemberAssignment assignment : assignments) {
            Member member = assignment.getMember();
            if (member != null && member.getId() != null) {
                accountRepository.findByMember_Id(member.getId()).ifPresent(memberAccount ->
                        putPeer(peers, memberAccount, "담당 회원")
                );
            }
            if (professional.getProfessionalType() == ProfessionalType.PHYSICIAN) {
                putPeer(peers, assignment.getTrainer(), "함께 맡은 트레이너");
            } else {
                putPeer(peers, assignment.getPhysician(), "함께 맡은 전문의");
            }
        }
    }

    private void putPeer(Map<Long, ChatPeerResponse> peers, Account account, String label) {
        if (account == null || account.getId() == null) {
            return;
        }
        peers.putIfAbsent(account.getId(), new ChatPeerResponse(
                account.getId(),
                account.getDisplayName(),
                label
        ));
    }

    private Account getAccount(Long accountId) {
        if (accountId == null) {
            throw new InvalidRequestException("대화 상대가 필요합니다.");
        }
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("대화 상대를 찾을 수 없습니다."));
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getSender().getDisplayName(),
                message.getBody(),
                message.getCreatedAt()
        );
    }

    private static boolean sameAccount(Account assigned, Account professional) {
        return assigned != null
                && assigned.getId() != null
                && Objects.equals(assigned.getId(), professional.getId());
    }
}
