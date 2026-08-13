from unittest.mock import AsyncMock, patch

import pytest

from app.schemas.document import SearchHit
from app.services.writer import write_answer


@pytest.mark.asyncio
async def test_write_answer_calls_llm_with_prompt():
    source = SearchHit(
        chunk_id="c1",
        document_id="d1",
        chunk_index=0,
        title="걷기 습관 안내",
        content="하루 20분 걷기부터 시작해 보세요.",
        distance=0.2,
    )
    with patch("app.services.writer.llm.chat", new_callable=AsyncMock) as chat:
        chat.return_value = "낮에 짧게 걸어 보세요."
        answer = await write_answer("걷기는 어떻게 시작하면 좋나요?", [source])

    assert answer == "낮에 짧게 걸어 보세요."
    chat.assert_awaited_once()
    _system, user_prompt = chat.await_args.args
    assert "걷기는 어떻게 시작하면 좋나요?" in user_prompt
    assert "하루 20분 걷기" in user_prompt 