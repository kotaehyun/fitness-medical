package com.fitnessmedical.dto.feedback;

import jakarta.validation.constraints.*;

/**
 * [공부/면접] 전문가 피드백 등록 요청 DTO
 *
 * <p><b>Q. Entity(Feedback)와 필드가 다른 이유는?</b><br>
 * A. memberId → URL {@code /members/{memberId}/feedbacks}에서 수신.<br>
 * writtenDate → "등록일은 서버 생성" 요구로 FeedbackService에서 {@code LocalDate.now()}.</p>
 *
 * <p><b>Q. @NotBlank + @Size 조합?</b><br>
 * A. @NotBlank로 null/공백 거부, @Size(min,max)로 길이 범위(예: content 10~1000자).</p>
 */
public record FeedbackRequest(

        @NotBlank(message = "작성자는 필수입니다.")
        @Size(max = 30, message = "작성자는 30자 이하여야 합니다.")
        String author,

        @NotBlank(message = "역할은 필수입니다.")
        @Size(max = 50, message = "역할은 50자 이하여야 합니다.")
        String role,

        @NotBlank(message = "내용은 필수입니다.")
        @Size(min = 10, max = 1000, message = "피드백 내용은 10자 이상 1000자 이하여야 합니다.")
        String content
) {
}
