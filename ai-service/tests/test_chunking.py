from app.services.chunking import split_text


def test_short_text_is_one_chunk():
    assert split_text("짧은 안내") == ["짧은 안내"]


def test_split_uses_overlap():
    text = "a" * 120
    chunks = split_text(text, chunk_size=50, overlap=10)
    assert len(chunks) == 3
    assert chunks[0] == "a" * 50
    assert chunks[1].startswith("a" * 10)


def test_empty_text_returns_empty_list():
    assert split_text("   ") == []
