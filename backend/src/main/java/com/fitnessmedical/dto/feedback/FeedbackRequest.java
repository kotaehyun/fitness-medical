package com.fitnessmedical.dto.feedback;

import jakarta.validation.constraints.*;

/**
 * [공부/면접] 전문가 피드백 등록 요청 DTO
 *
 * <p><b>Q. Entity(Feedback)와 필드가 다른 이유는?</b><br>
 * A. memberId → URL {@code /members/{memberId}/feedback}에서 수신.<br>
 * writtenDate → 서버 {@code LocalDate.now()}.<br>
 * author·role → 세션 Account(displayName / 전문가). 클라이언트가 위조하지 못하게 DTO에서 제외.</p>
 *
 * <p><b>Q. @NotBlank + @Size 조합?</b><br>
 * A. @NotBlank로 null/공백 거부, @Size(min,max)로 길이 범위(예: content 10~1000자).</p>
 */
public record FeedbackRequest(

        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 10, max = 1000, message = "피드백 내용은 10자 이상 1000자 이하여야 합니다.")
        String content
) {
}
