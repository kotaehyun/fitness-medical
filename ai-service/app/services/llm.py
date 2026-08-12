"""
[공부/면접] 로컬 Ollama 호출.

Q. OpenAI SDK를 안 쓰는 이유는?
A. 지금은 Ollama HTTP면 충분하다. httpx로 /api/chat 을 친다.
   나중에 OpenAI 호환이면 base_url만 바꾸면 된다.

Q. stream=false ?
A. 학습용 API는 완성된 문장을 한 번에 받는다.
   프론트 스트리밍은 다음 단계에서 붙인다.
"""

import httpx
from fastapi import HTTPException

from app.core.config import settings


async def is_ollama_ready() -> bool:
    url = f"{settings.ollama_base_url.rstrip('/')}/api/tags"
    try:
        async with httpx.AsyncClient(timeout=2.0) as client:
            response = await client.get(url)
        return response.status_code == 200
    except Exception:
        return False


async def chat(system_prompt: str, user_prompt: str) -> str:
    url = f"{settings.ollama_base_url.rstrip('/')}/api/chat"
    payload = {
        "model": settings.llm_model,
        "stream": False,
        "messages": [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_prompt},
        ],
    }
    try:
        async with httpx.AsyncClient(timeout=settings.llm_timeout_seconds) as client:
            response = await client.post(url, json=payload)
    except httpx.RequestError as exc:
        raise HTTPException(
            status_code=503,
            detail="Ollama에 연결되지 않았습니다.",
        ) from exc

    if response.status_code >= 400:
        raise HTTPException(
            status_code=503,
            detail="Ollama 응답에 실패했습니다.",
        )

    body = response.json()
    message = body.get("message") or {}
    content = (message.get("content") or "").strip()
    if not content:
        raise HTTPException(
            status_code=503,
            detail="Ollama가 빈 응답을 반환했습니다.",
        )
    return content
