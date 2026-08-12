"""
[공부/면접] Document 업무 규칙. Spring Service와 같다.

Q. 왜 Router가 split_text를 직접 안 부르나?
A. HTTP는 Router, DB는 Repository, 분할 규칙은 Service.
   나중에 재분할·중복 방지 같은 규칙이 생겨도 Router는 얇게 유지한다.

Q. 왜 기존 Chunk를 지우고 다시 넣나?
A. 같은 문서를 두 번 split하면 조각이 쌓인다.
   재분할은 "최신 본문 기준 1세트"가 맞다.

Q. 왜 get → split → 검증 다음에 delete 하나?
A. 문서가 없거나(404) 본문이 비면(400) 기존 Chunk를 지우면 안 된다.
   저장할 조각이 준비된 뒤에만 교체한다.

Q. Mongo 다음에 Chroma를 쓰는 이유는?
A. Mongo가 원문 정본이다. 벡터 저장이 실패해도 GET /chunks 는 살아 있다.
   검색은 Chroma가 준비된 뒤에만 동작한다.
"""

import asyncio

from fastapi import HTTPException
from chromadb.api.models.Collection import Collection

from app.repositories.document_repository import DocumentRepository
from app.schemas.document import ChunkCreate, ChunkResponse
from app.services.chunking import split_text
from app.services.vector_store import VectorStore


class DocumentService:
    def __init__(
        self,
        repo: DocumentRepository,
        collection: Collection | None,
    ) -> None:
        self._repo = repo
        self._vectors = VectorStore(collection)

    async def split_document(self, document_id: str) -> list[ChunkResponse]:
        document = await self._repo.get(document_id)
        pieces = split_text(document.content)
        if not pieces:
            raise HTTPException(
                status_code=400,
                detail="분할할 본문이 없습니다.",
            )

        await self._repo.delete_chunks(document_id)
        chunks: list[ChunkResponse] = []
        for index, piece in enumerate(pieces):
            chunk = await self._repo.create_chunk(
                ChunkCreate(
                    document_id=document.id,
                    chunk_index=index,
                    content=piece,
                )
            )
            chunks.append(chunk)

        await asyncio.to_thread(
            self._vectors.replace_document_chunks,
            document.id,
            chunks,
            title=document.title,
        )
        return chunks
