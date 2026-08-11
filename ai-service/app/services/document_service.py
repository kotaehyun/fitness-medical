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
"""

from fastapi import HTTPException

from app.repositories.document_repository import DocumentRepository
from app.schemas.document import ChunkCreate, ChunkResponse
from app.services.chunking import split_text


class DocumentService:
    def __init__(self, repo: DocumentRepository) -> None:
        self._repo = repo

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
        return chunks