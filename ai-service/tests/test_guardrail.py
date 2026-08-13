from app.services.guardrail import (
    is_blocked_answer,
    is_blocked_query,
)


def test_blocks_diagnosis_query():
    assert is_blocked_query("저 당뇨인가요? 진단해주세요")


def test_allows_lifestyle_query():
    assert not is_blocked_query("잠은 어떻게 자면 좋나요?")


def test_blocks_prescription_answer():
    assert is_blocked_answer("혈압약이 필요하니 하루 5mg 복용하세요.")


def test_allows_coaching_answer():
    assert not is_blocked_answer("규칙적인 수면과 산책이 도움이 될 수 있어요.")
