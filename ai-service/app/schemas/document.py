"""
[공부/면접] RAG Document / Chunk 스키마. Spring의 DTO(record)와 같은 역할.

Q. Document와 Chunk를 나누는 이유는?
A. 원문(Document)은 출처 단위로 보관하고, 검색은 짧은 Chunk 단위로 한다.
   임베딩은 Chunk에 붙이는 편이 정확도가 높다.

Q. Pydantic BaseModel vs Java record?
A. 둘 다 요청/응답 검증·직렬화용. FastAPI는 BaseModel로 OpenAPI 스키마를 자동 생성한다.

주의: content는 생활·코칭 안내 문구. 진단·처방 단정 문서를 넣지 않는다.
"""

from datetime import datetime

from pydantic import BaseModel, Field


class RagDocumentCreate(BaseModel):
    title: str = Field(min_length=1, max_length=200)
    source: str = Field(min_length=1, max_length=200)
    category: str = Field(min_length=1, max_length=500)
    content: str = Field(min_length=1, max_length=50000)


class RagDocumentResponse(BaseModel):
    id: str
    title: str
    source: str
    category: str
    content: str
    created_at: datetime
    updated_at: datetime


class ChunkCreate(BaseModel):
    document_id: str
    chunk_index: int = Field(ge=0)
    content: str = Field(min_length=1, max_length=8000)


class ChunkResponse(BaseModel):
    id: str
    document_id: str
    chunk_index: int
    content: str
    created_at: datetime


class SearchRequest(BaseModel):
    query: str = Field(min_length=1, max_length=500)
    n_results: int = Field(default=5, ge=1, le=20)


class SearchHit(BaseModel):
    chunk_id: str
    document_id: str
    chunk_index: int
    title: str
    content: str
    distance: float | None = None
