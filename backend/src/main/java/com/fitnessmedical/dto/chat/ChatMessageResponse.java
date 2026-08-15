package com.fitnessmedical.dto.chat;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long senderAccountId,
        Long receiverAccountId,
        String senderDisplayName,
        String body,
        LocalDateTime createdAt
) {
}
