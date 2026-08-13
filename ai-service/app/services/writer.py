"""
[공부/면접] Writer 에이전트. 검색 근거로 문장을 만든다.

Q. Retriever와 역할 차이는?
A. Retriever는 Chunk를 찾고, Writer는 그 Chunk만으로 답을 쓴다.
   근거가 없으면 여기서 LLM을 부르지 않는다. (AskService가 먼저 막는다)

Q. 왜 prompt 조립을 Writer에 두나?
A. 생성에 필요한 컨텍스트는 Writer 책임이다.
   AskService는 Router → Retriever → Writer → Guard 순서만 맞춘다.
"""

from app.schemas.document import SearchHit
from app.services import llm
from app.services.prompt import SYSTEM_PROMPT, build_user_prompt


async def write_answer(query: str, sources: list[SearchHit]) -> str:
    return await llm.chat(
        SYSTEM_PROMPT,
        build_user_prompt(query, sources),
    )