"""
[공부/면접] RAG 질문 API.

Q. /search 와 /ask 차이?
A. /search 는 비슷한 Chunk만. /ask 는 그 Chunk를 근거로 문장을 만든다.
   생성 결과가 진단처럼 보이면 안 되므로 프롬프트에서 금지한다.
"""

from fastapi import APIRouter

from app.core.chroma import get_collection
from app.schemas.document import AskRequest, AskResponse
from app.services.ask_service import AskService

router = APIRouter(tags=["ask"])


@router.post("/ask", response_model=AskResponse)
async def ask(payload: AskRequest) -> AskResponse:
    return await AskService(get_collection()).ask(
        payload.query,
        payload.n_results,
    )
