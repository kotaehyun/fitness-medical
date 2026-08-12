"""
[공부/면접] 벡터 검색 API.

Q. 왜 GET이 아니라 POST인가?
A. query 본문이 길어질 수 있고, 검색은 조회처럼 보여도
   임베딩 계산이 들어가므로 POST /search 로 둔다.

Q. LLM은 어디 있나?
A. 아직 없다. 이 API는 "비슷한 Chunk"만 돌려준다.
   답변 생성은 다음 단계에서 검색 결과를 컨텍스트로 넘긴다.

주의: 검색 결과를 진단·처방으로 해석하지 않는다.
"""

import asyncio

from fastapi import APIRouter

from app.core.chroma import get_collection
from app.schemas.document import SearchHit, SearchRequest
from app.services.vector_store import VectorStore

router = APIRouter(tags=["search"])


@router.post("/search", response_model=list[SearchHit])
async def search(payload: SearchRequest) -> list[SearchHit]:
    store = VectorStore(get_collection())
    return await asyncio.to_thread(
        store.query,
        payload.query,
        payload.n_results,
    )
