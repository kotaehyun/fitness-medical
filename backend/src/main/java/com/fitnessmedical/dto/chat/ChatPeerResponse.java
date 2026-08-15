package com.fitnessmedical.dto.chat;

/**
 * 대화 상대. accountId는 이후 GET/POST /api/messages 의 peerAccountId.
 */
public record ChatPeerResponse(
        Long accountId,
        String displayName,
        String peerLabel
) {
}
