from app.schemas.document import SearchHit
from app.services.prompt import NO_SOURCE_ANSWER, SYSTEM_PROMPT, build_user_prompt


def _hit(title: str, content: str) -> SearchHit:
    return SearchHit(
        chunk_id="c1",
        document_id="d1",
        chunk_index=0,
        title=title,
        content=content,
        distance=0.1,
    )


def test_user_prompt_includes_query_and_source():
    prompt = build_user_prompt(
        "수면은 어떻게 하나요?",
        [_hit("수면 습관 안내", "규칙적인 수면은 회복에 도움이 됩니다.")],
    )
    assert "수면은 어떻게 하나요?" in prompt
    assert "수면 습관 안내" in prompt
    assert "규칙적인 수면" in prompt


def test_system_prompt_forbids_diagnosis():
    assert "진단" in SYSTEM_PROMPT
    assert "처방" in SYSTEM_PROMPT


def test_no_source_answer_is_not_medical():
    assert "진단" in NO_SOURCE_ANSWER or "처방" in NO_SOURCE_ANSWER
