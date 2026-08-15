package com.fitnessmedical.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * [공부/면접] 메시지 전송. sender는 세션 Account. 클라이언트가 위조하지 않는다.
 */
public record ChatMessageCreateRequest(

        @NotNull(message = "대화 상대가 필요합니다.")
        Long peerAccountId,

        @NotBlank(message = "내용을 입력해 주세요.")
        @Size(max = 1000, message = "메시지는 1000자 이하여야 합니다.")
        String body
) {
}
