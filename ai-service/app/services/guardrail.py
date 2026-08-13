"""
[공부/면접] 생활 안내 가드레일 (규칙 기반)

Q. 왜 LLM 라우터 대신 키워드인가?
A. 지금은 빠르고 결정적(deterministic)인 차단이 우선이다.
   의료 진단·처방 요청을 모델에 넣기 전에 끊는다.

Q. 멀티 에이전트와의 관계?
A. Router/Guard 역할만 코드로 분리한 형태다. 모델은 하나(Answer)만 쓴다.
"""

from __future__ import annotations

import re

# 질문에서 막는다: 진단·처방·약 용량 등을 직접 요구하는 표현
_QUERY_BLOCK_PATTERNS: tuple[re.Pattern[str], ...] = tuple(
    re.compile(p) for p in (
        r"진단",
        r"처방",
        r"병명",
        r"질병\s*이름",
        r"무슨\s*병",
        r"약\s*용량",
        r"몇\s*mg",
        r"투약",
        r"치료\s*법",
        r"수술\s*해야",
        r"암\s*인가",
        r"당뇨\s*인가",
        r"고혈압\s*인가",
    )
)

# 답변에서 막는다: 모델이 금지 문장을 내보낸 경우
_ANSWER_BLOCK_PATTERNS: tuple[re.Pattern[str], ...] = tuple(
    re.compile(p) for p in (
        r"진단합니다",
        r"진단\s*결과",
        r"처방합니다",
        r"처방전",
        r"\d+\s*mg",
        r"복용하세요",
        r"약을\s*드세요",
        r"치료가\s*필요합니다",
    )
)

BLOCKED_QUERY_ANSWER = (
    "의료 진단·처방·약 용량에 대한 질문은 답할 수 없습니다. "
    "생활·운동 습관 관련 안내만 도와드릴 수 있어요. "
    "증상이나 치료가 걱정되면 전문가와 상담해 주세요."
)

BLOCKED_ANSWER_FALLBACK = (
    "생성된 안내에 의료적 단정 표현이 포함되어 보여 드리지 않았습니다. "
    "생활 습관 질문으로 다시 물어봐 주세요. "
    "이 서비스는 진단이나 처방을 하지 않습니다."
)


def is_blocked_query(query: str) -> bool:
    text = (query or "").strip()
    if not text:
        return False
    return any(pattern.search(text) for pattern in _QUERY_BLOCK_PATTERNS)


def is_blocked_answer(answer: str) -> bool:
    text = (answer or "").strip()
    if not text:
        return False
    return any(pattern.search(text) for pattern in _ANSWER_BLOCK_PATTERNS)
