from unittest.mock import MagicMock, patch

import pytest

from app.schemas.document import SearchHit
from app.services.retriever import Retriever


@pytest.mark.asyncio
async def test_retrieve_delegates_to_vector_store():
    with patch("app.services.retriever.VectorStore") as store_cls:
        store = MagicMock()
        store.query.return_value = []
        store_cls.return_value = store

        hits = await Retriever(None).retrieve("걷기 습관", 5)

        assert hits == []
        store.query.assert_called_once_with("걷기 습관", 5)


@pytest.mark.asyncio
async def test_retrieve_drops_far_hits():
    near = SearchHit(
        chunk_id="c1",
        document_id="d1",
        chunk_index=0,
        title="걷기",
        content="하루 20분",
        distance=0.39,
    )
    far = SearchHit(
        chunk_id="c2",
        document_id="d2",
        chunk_index=0,
        title="수면",
        content="같은 시간에 잠드세요",
        distance=0.80,
    )
    with patch("app.services.retriever.VectorStore") as store_cls:
        store = MagicMock()
        store.query.return_value = [near, far]
        store_cls.return_value = store
        hits = await Retriever(None).retrieve("걷기 습관", 5)

    assert hits == [near]
