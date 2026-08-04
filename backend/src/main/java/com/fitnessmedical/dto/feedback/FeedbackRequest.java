package com.fitnessmedical.dto.feedback;


import jakarta.validation.constraints.*;

/**
 * 전문가 피드백 등록 요청 DTO
 *
 *
 * 설계 결정:
 *  - member → DTO에 안 넣음. URL의 {memberId}로 따로 받음 (HealthRecordRequest와 같은 패턴).
 *  - writtenDate → DTO에 안 넣음. "등록일은 서버에서 생성" 요구사항에 따라
 *    FeedbackService에서 LocalDate.now()로 채움.
 */

public record FeedbackRequest (

     // 작성자 : 비어 있으면 안 됨
     @NotBlank String author,

     // 역할: 비어 있으면 안 됨 (예: "재활의학 전문가", "운동 전문가" )
     @NotBlank String role,

     // 내용: 비어 있으면 안 되고, 10자 이상 이어야 함
     // @Size는 문자열 의 길이 범위를 검사 합니다 (min/max).
     @NotBlank @Size(min = 10) String content
) {
}
