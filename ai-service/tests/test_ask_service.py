from unittest.mock import AsyncMock, MagicMock, patch

import pytest

from app.schemas.document import SearchHit
from app.services.ask_service import AskService
from app.services.intent_router import AskIntent
from app.services.prompt import NO_SOURCE_ANSWER


@pytest.mark.asyncio
async def test_blocked_query_skips_retrieve_and_write():
    with (
        patch("app.services.ask_service.route_query", return_value=AskIntent.BLOCKED),
        patch("app.services.ask_service.Retriever") as retriever_cls,
        patch("app.services.ask_service.write_answer", new_callable=AsyncMock) as write,
    ):
        retriever = MagicMock()
        retriever.retrieve = AsyncMock()
        retriever_cls.return_value = retriever

        result = await AskService(None).ask("저 당뇨인가요? 진단해주세요", 5)

    assert result.model == "guardrail"
    assert result.sources == []
    retriever.retrieve.assert_not_awaited()
    write.assert_not_awaited()


@pytest.mark.asyncio
async def test_no_sources_skips_writer():
    with (
        patch("app.services.ask_service.route_query", return_value=AskIntent.LIFESTYLE),
        patch("app.services.ask_service.Retriever") as retriever_cls,
        patch("app.services.ask_service.write_answer", new_callable=AsyncMock) as write,
    ):
        retriever = MagicMock()
        retriever.retrieve = AsyncMock(return_value=[])
        retriever_cls.return_value = retriever

        result = await AskService(None).ask("잠은 어떻게 자면 좋나요?", 5)

    assert result.model == "none"
    assert result.answer == NO_SOURCE_ANSWER
    write.assert_not_awaited()


@pytest.mark.asyncio
async def test_lifestyle_query_uses_writer():
    hit = SearchHit(
        chunk_id="c1",
        document_id="d1",
        chunk_index=0,
        title="걷기",
        content="하루 20분 걷기부터 시작해 보세요.",
        distance=0.2,
    )
    with (
        patch("app.services.ask_service.route_query", return_value=AskIntent.LIFESTYLE),
        patch("app.services.ask_service.Retriever") as retriever_cls,
        patch("app.services.ask_service.write_answer", new_callable=AsyncMock) as write,
        patch("app.services.ask_service.is_blocked_answer", return_value=False),
    ):
        retriever = MagicMock()
        retriever.retrieve = AsyncMock(return_value=[hit])
        retriever_cls.return_value = retriever
        write.return_value = "낮에 짧게 걸어 보세요."

        result = await AskService(None).ask("걷기는 어떻게 시작하면 좋나요?", 5)

    write.assert_awaited_once_with("걷기는 어떻게 시작하면 좋나요?", [hit])
    assert result.answer == "낮에 짧게 걸어 보세요."
    assert result.sources == [hit]
