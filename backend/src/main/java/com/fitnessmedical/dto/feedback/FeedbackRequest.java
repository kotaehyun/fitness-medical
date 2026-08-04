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
 *  - 검증 어노테이션은 맥북에서 별도로 진행한 버전(길이 상한 + 커스텀 메시지)이 더 꼼꼼해서
 *    그쪽 기준으로 통일함 (2026-08-04).
 */

public record FeedbackRequest (

     // 작성자: 비어 있으면 안 되고, 30자를 넘으면 안 됨
     @NotBlank(message = "작성자는 필수입니다.")
     @Size(max = 30, message = "작성자는 30자 이하여야 합니다.")
     String author,

     // 역할: 비어 있으면 안 되고, 50자를 넘으면 안 됨 (예: "재활의학 전문가", "운동 전문가")
     @NotBlank(message = "역할은 필수입니다.")
     @Size(max = 50, message = "역할은 50자 이하여야 합니다.")
     String role,

     // 내용: 비어 있으면 안 되고, 10자 이상 1000자 이하여야 함
     // @Size는 문자열의 길이 범위를 검사합니다 (min/max).
     @NotBlank(message = "내용은 필수입니다.")
     @Size(min = 10, max = 1000, message = "피드백 내용은 10자 이상 1000자 이하여야 합니다.")
     String content
) {
}
