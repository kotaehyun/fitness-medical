package com.fitnessmedical.dto.feedback;

import com.fitnessmedical.entity.Feedback;
import java.time.LocalDate;

/**
 * [공부/면접] 피드백 조회 응답 DTO
 *
 * <p><b>Q. Entity vs FeedbackResponse?</b><br>
 * A. Entity는 {@link com.fitnessmedical.entity.Member} FK 객체를 보유.
 * DTO는 memberId(Long)와 표시용 문자열만 JSON으로 직렬화합니다.</p>
 *
 * <p><b>Q. record + from() 패턴을 쓰는 이유?</b><br>
 * A. 불변 응답 객체 + Entity→DTO 변환을 타입 안전하게 캡슐화.</p>
 */
public record FeedbackResponse(
        Long id,
        Long memberId,
        String author,
        String role,
        LocalDate writtenDate,
        String content
) {
    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(), feedback.getMember().getId(), feedback.getAuthor(),
                feedback.getRole(), feedback.getWrittenDate(), feedback.getContent()
        );
    }
}
