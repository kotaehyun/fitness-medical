"""
[공부/면접] RAG 질문 흐름.

Router → Retriever → Writer(LLM) → Guard

Q. 왜 검색과 생성을 한 API로 묶나?
A. 클라이언트가 Chunk를 다시 붙여 보내지 않게 한다.
   출처(sources)는 함께 내려서 어떤 문서를 봤는지 확인한다.

Q. 가드레일은 언제 도나?
A. Router가 BLOCKED면 검색·LLM 전에 끊는다. 답변 점검은 LLM 직후에 한다.
   진단·처방 요청은 모델을 호출하지 않는다.
"""

from chromadb.api.models.Collection import Collection

from app.core.config import settings
from app.schemas.document import AskResponse
from app.services.guardrail import (
    BLOCKED_ANSWER_FALLBACK,
    BLOCKED_QUERY_ANSWER,
    is_blocked_answer,
)
from app.services.intent_router import AskIntent, route_query
from app.services.prompt import NO_SOURCE_ANSWER
from app.services.writer import write_answer
from app.services.retriever import Retriever


class AskService:
    def __init__(self, collection: Collection | None) -> None:
        self._retriever = Retriever(collection)

    async def ask(self, query: str, n_results: int) -> AskResponse:
        if route_query(query) is AskIntent.BLOCKED:
            return AskResponse(
                answer=BLOCKED_QUERY_ANSWER,
                model="guardrail",
                sources=[],
            )

        sources = await self._retriever.retrieve(query, n_results)
        if not sources:
            return AskResponse(
                answer=NO_SOURCE_ANSWER,
                model="none",
                sources=[],
            )

        answer = await write_answer(query, sources)

        if is_blocked_answer(answer):
            return AskResponse(
                answer=BLOCKED_ANSWER_FALLBACK,
                model="guardrail",
                sources=sources,
            )

        return AskResponse(
            answer=answer,
            model=settings.llm_model,
            sources=sources,
        )
