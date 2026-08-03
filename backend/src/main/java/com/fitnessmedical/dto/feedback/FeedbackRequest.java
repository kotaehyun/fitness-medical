package com.fitnessmedical.dto.feedback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(

        @NotBlank(message = "작성자는 필수입니다.")
        @Size(max = 30, message = "작성자는 30자 이하여야 합니다.")
        String author,

        @NotBlank(message = "역할은 필수입니다.")
        @Size(max = 50, message = "역할은 50자 이하여야 합니다.")
        String role,

        @NotBlank(message = "피드백 내용은 필수입니다.")
        @Size(
                min = 10,
                max = 1000,
                message = "피드백 내용은 10자 이상 1000자 이하여야 합니다."
        )
        String content
) {
}