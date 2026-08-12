"""
[공부/면접] RAG 프롬프트.

Q. 왜 검색 결과를 질문에 붙이나?
A. LLM은 우리 DB를 모른다. 관련 Chunk를 컨텍스트로 넣어야
   "검색된 안내만" 말할 수 있다. 이게 RAG의 핵심이다.

Q. 근거가 없을 때 모델을 호출하지 않는 이유는?
A. 빈 컨텍스트로 부르면 학습 데이터로 지어낸다.
   생활 안내라도 없는 근거를 단정하면 안 된다.
"""

from app.schemas.document import SearchHit

SYSTEM_PROMPT = """당신은 Fitness Medical의 생활·운동 습관 안내 도우미입니다.
의료 진단, 질병명 단정, 처방, 약 용량, 치료 지시를 하지 마세요.
검색된 근거에만 답하고, 근거에 없으면 모른다고 하세요.
일반적인 생활 습관 안내와 전문가 상담 권유만 하세요.
답변은 한국어로, 짧게 작성하세요."""

NO_SOURCE_ANSWER = (
    "관련 안내 문서를 찾지 못했습니다. "
    "생활 습관 문서를 등록·분할한 뒤 다시 질문해 주세요. "
    "이 안내는 의료 진단이나 처방이 아닙니다."
)


def build_user_prompt(query: str, sources: list[SearchHit]) -> str:
    blocks: list[str] = []
    for index, hit in enumerate(sources, start=1):
        title = hit.title or "(제목 없음)"
        blocks.append(f"[{index}] 제목: {title}\n{hit.content}")
    context = "\n\n".join(blocks)
    return (
        f"질문: {query}\n\n"
        f"근거:\n{context}\n\n"
        "위 근거만 사용해 답하세요. 근거 밖의 사실은 추가하지 마세요."
    )
