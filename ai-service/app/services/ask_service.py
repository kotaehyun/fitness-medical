"""
[공부/면접] RAG 질문 흐름.

검색(Chroma) → 근거 확인 → 프롬프트 → LLM(Ollama) → 답변+출처

Q. 왜 검색과 생성을 한 API로 묶나?
A. 클라이언트가 Chunk를 다시 붙여 보내지 않게 한다.
   출처(sources)는 함께 내려서 어떤 문서를 봤는지 확인한다.
"""

import asyncio

from chromadb.api.models.Collection import Collection

from app.core.config import settings
from app.schemas.document import AskResponse, SearchHit
from app.services import llm
from app.services.prompt import NO_SOURCE_ANSWER, SYSTEM_PROMPT, build_user_prompt
from app.services.vector_store import VectorStore


class AskService:
    def __init__(self, collection: Collection | None) -> None:
        self._vectors = VectorStore(collection)

    async def ask(self, query: str, n_results: int) -> AskResponse:
        sources: list[SearchHit] = await asyncio.to_thread(
            self._vectors.query,
            query,
            n_results,
        )
        if not sources:
            return AskResponse(
                answer=NO_SOURCE_ANSWER,
                model="none",
                sources=[],
            )

        answer = await llm.chat(
            SYSTEM_PROMPT,
            build_user_prompt(query, sources),
        )
        return AskResponse(
            answer=answer,
            model=settings.llm_model,
            sources=sources,
        )
