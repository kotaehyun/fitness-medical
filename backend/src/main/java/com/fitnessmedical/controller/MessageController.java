package com.fitnessmedical.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fitnessmedical.dto.chat.ChatMessageCreateRequest;
import com.fitnessmedical.dto.chat.ChatMessageResponse;
import com.fitnessmedical.dto.chat.ChatPeerResponse;
import com.fitnessmedical.service.MessageService;

import jakarta.validation.Valid;

/**
 * [공부/면접] 텍스트 메시지. 권한은 MessageService가 담당 연결로 검사한다.
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/peers")
    public List<ChatPeerResponse> listPeers(@AuthenticationPrincipal UserDetails user) {
        return messageService.listPeers(loginId(user));
    }

    @GetMapping
    public List<ChatMessageResponse> listMessages(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam Long peerAccountId
    ) {
        return messageService.listMessages(loginId(user), peerAccountId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChatMessageResponse send(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody ChatMessageCreateRequest request
    ) {
        return messageService.send(loginId(user), request);
    }

    private static String loginId(UserDetails user) {
        return user == null ? null : user.getUsername();
    }
}
