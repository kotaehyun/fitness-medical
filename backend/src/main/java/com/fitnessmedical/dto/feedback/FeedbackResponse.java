package com.fitnessmedical.dto.feedback;

import com.fitnessmedical.entity.Feedback;
import java.time.LocalDate;

public record FeedbackResponse(
        Long id, Long memberId, String author, String role, LocalDate writtenDate, String content
) {
    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(), feedback.getMember().getId(), feedback.getAuthor(),
                feedback.getRole(), feedback.getWrittenDate(), feedback.getContent()
        );
    }
}
