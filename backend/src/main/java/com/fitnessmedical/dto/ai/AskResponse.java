package com.fitnessmedical.dto.ai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * [공부/면접] AI 답변 응답
 *
 * <p>Q. {@code @JsonAlias}는?<br>
 * A. FastAPI JSON은 snake_case({@code chunk_id})다. 역직렬화 때 별칭으로 받고,
 * Spring → 프론트 응답은 camelCase({@code chunkId})로 나간다.</p>
 */
public record AskResponse(
        String answer,
        String model,
        List<SearchHit> sources
) {

    public record SearchHit(
            @JsonAlias("chunk_id") String chunkId,
            @JsonAlias("document_id") String documentId,
            @JsonAlias("chunk_index") int chunkIndex,
            String title,
            String content,
            Double distance
    ) {
    }
}
