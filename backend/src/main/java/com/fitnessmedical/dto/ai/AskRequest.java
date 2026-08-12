package com.fitnessmedical.dto.ai;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * [공부/면접] Spring API용 질문 요청 (프론트 → Backend)
 *
 * <p>FastAPI 쪽 필드명은 {@code n_results}(snake_case)이지만,
 * 이 DTO는 기존 Spring API처럼 camelCase {@code nResults}를 쓴다.
 * 변환은 {@code AiAskService}에서 한다.</p>
 */
public record AskRequest(
        @NotBlank(message = "질문을 입력해 주세요.")
        @Size(max = 500, message = "질문은 500자 이하여야 합니다.")
        String query,

        @Min(value = 1, message = "nResults는 1 이상이어야 합니다.")
        @Max(value = 20, message = "nResults는 20 이하여야 합니다.")
        Integer nResults
) {
}
