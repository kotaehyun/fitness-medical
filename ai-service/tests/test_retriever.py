from unittest.mock import MagicMock, patch

import pytest

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
