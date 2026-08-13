"""
    [공부 / 면접]

    Q. 왜 별도 파일인가?
    A. Guard(차단)와 Router(어디로 보낼지)를 나눈다.
       지금은 키워드, 나중에 LLM 라우터로 바꿔도 AskService는 얇게 유지한다.

    Q. BLOCKED면 검색을 하나?
    A. 하지 않는다. 진단·처방 요청은 Retriever/Writer에 넣지 않는다.
"""

from enum import Enum
from app.services.guardrail import is_blocked_query



class AskIntent(str, Enum):
    LIFESTYLE = "lifestyle"
    BLOCKED = "blocked"


def route_query(query: str) -> AskIntent:
    if is_blocked_query(query):
        return AskIntent.BLOCKED
    return AskIntent.LIFESTYLE


