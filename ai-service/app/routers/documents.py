"""
[공부/면접] Document HTTP 계층. Spring Controller와 같다.

Q. prefix="/documents" ?
A. 이 라우터의 모든 경로는 /documents 아래로 붙는다.
   POST "" → POST /documents, GET "" → GET /documents

Q. Router가 Repository를 직접 호출해도 되나?
A. 학습 초기에는 가능. 분할(split) 같은 규칙이 생기면 Service를 둔다.
   Spring과 같이 Controller → Service → Repository가 면접에서 설명하기 쉽다.

Q. GET .../chunks 와 POST .../split 차이?
A. GET은 조회만. POST /split은 본문을 나누어 Chunk를 만든다(부작용 있음).
   경로가 같아도 메서드로 구분한다. split은 GET이 아니라 POST.
"""

from fastapi import APIRouter, Query

from app.core.chroma import get_collection
from app.core.database import get_database
from app.repositories.document_repository import DocumentRepository
from app.schemas.document import (
    ChunkResponse,
    RagDocumentCreate,
    RagDocumentResponse,
)
from app.services.document_service import DocumentService

router = APIRouter(
    prefix="/documents",
    tags=["documents"],
)


def _repo() -> DocumentRepository:
    return DocumentRepository(get_database())

def _service() -> DocumentService:
    return DocumentService(_repo(), get_collection())


@router.post("", response_model=RagDocumentResponse, status_code=201)
async def create_document(payload: RagDocumentCreate) -> RagDocumentResponse:
    return await _repo().create(payload)


@router.get("", response_model=list[RagDocumentResponse])
async def list_documents(
    limit: int = Query(default=50, ge=1, le=200),
) -> list[RagDocumentResponse]:
    return await _repo().list(limit=limit)


@router.get("/{document_id}", response_model=RagDocumentResponse)
async def get_document(document_id: str) -> RagDocumentResponse:
    return await _repo().get(document_id)



@router.get("/{document_id}/chunks", response_model=list[ChunkResponse])
async def list_chunks(document_id: str) -> list[ChunkResponse]:
    return await _repo().list_chunks(document_id)


@router.post(
    "/{document_id}/split",
    response_model=list[ChunkResponse],
    status_code=201,
)
async def split_document(document_id: str) -> list[ChunkResponse]:
    return await _service().split_document(document_id)
