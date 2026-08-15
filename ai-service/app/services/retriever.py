"""
[공부/면접] Retriever 에이전트. Chroma에서 근거 Chunk를 가져온다.

Q. VectorStore와 뭐가 다르나?
A. VectorStore는 저장·검색 인프라. Retriever는 Ask 파이프라인의 검색 역할.
   Router → Retriever → Writer → Guard 로 설명할 때 이 파일이 Retriever다.

Q. 왜 async + to_thread인가?
A. Chroma query는 동기라 이벤트 루프를 막지 않으려고 스레드로 넘긴다.

Q. MAX_DISTANCE는?
A. cosine 거리. 작을수록 비슷하다. 너무 먼 Chunk는 Writer에 안 넘긴다.
"""

import asyncio

from chromadb.api.models.Collection import Collection

from app.schemas.document import SearchHit
from app.services.vector_store import VectorStore

# 학습용 임계값. 걷기(≈0.39)는 남기고 관계없는 조각(≈0.80)은 버린다.
MAX_DISTANCE = 0.55


class Retriever:
    def __init__(self, collection: Collection | None) -> None:
        self._store = VectorStore(collection)

    async def retrieve(self, query: str, n_results: int) -> list[SearchHit]:
        hits = await asyncio.to_thread(self._store.query, query, n_results)
        return [
            hit
            for hit in hits
            if hit.distance is None or hit.distance <= MAX_DISTANCE
        ]
