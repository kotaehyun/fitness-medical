"""
[공부/면접] MongoDB Repository. Spring Data의 JpaRepository와 같은 계층.

Q. 컬렉션 documents / chunks ?
A. Mongo는 테이블 대신 컬렉션. documents=원문, chunks=조각.
   chunks.document_id로 부모 Document를 참조한다.

Q. ObjectId는?
A. Mongo 기본 PK. JSON으로는 str로 내려준다.
   잘못된 문자열이면 is_valid로 400, 없으면 find_one → 404.

Q. async for 를 쓰는 이유?
A. motor 커서는 비동기 이터레이터다. 일반 for는 동작하지 않는다.

Q. delete_many vs delete_one?
A. 한 문서의 Chunk는 여러 개다. 재분할 때 전부 지워야 하므로 delete_many.
   원문(documents)은 지우지 않는다.
"""

from datetime import UTC, datetime

from bson import ObjectId
from fastapi import HTTPException
from motor.motor_asyncio import AsyncIOMotorDatabase

from app.schemas.document import (
    ChunkCreate,
    ChunkResponse,
    RagDocumentCreate,
    RagDocumentResponse,
)

DOCUMENTS = "documents"
CHUNKS = "chunks"


def _require_db(db: AsyncIOMotorDatabase | None) -> AsyncIOMotorDatabase:
    # 연결 실패를 500(서버 버그)이 아니라 503(일시 불가)로 표현한다.
    if db is None:
        raise HTTPException(
            status_code=503,
            detail="MongoDB에 연결되지 않았습니다.",
        )
    return db


def _to_response(doc: dict) -> RagDocumentResponse:
    return RagDocumentResponse(
        id=str(doc["_id"]),
        title=doc["title"],
        source=doc["source"],
        category=doc["category"],
        content=doc["content"],
        created_at=doc["created_at"],
        updated_at=doc["updated_at"],
    )


def _to_chunk_response(chunk: dict) -> ChunkResponse:
    return ChunkResponse(
        id=str(chunk["_id"]),
        document_id=str(chunk["document_id"]),
        chunk_index=chunk["chunk_index"],
        content=chunk["content"],
        created_at=chunk["created_at"],
    )


class DocumentRepository:
    def __init__(self, db: AsyncIOMotorDatabase | None) -> None:
        self._db = db

    async def create(self, payload: RagDocumentCreate) -> RagDocumentResponse:
        db = _require_db(self._db)
        now = datetime.now(UTC)
        document = {
            "title": payload.title,
            "source": payload.source,
            "category": payload.category,
            "content": payload.content,
            "created_at": now,
            "updated_at": now,
        }
        result = await db[DOCUMENTS].insert_one(document)
        document["_id"] = result.inserted_id
        return _to_response(document)

    async def list(self, limit: int = 50) -> list[RagDocumentResponse]:
        db = _require_db(self._db)
        cursor = db[DOCUMENTS].find().sort("created_at", -1).limit(limit)
        return [_to_response(doc) async for doc in cursor]

    async def get(self, document_id: str) -> RagDocumentResponse:
        db = _require_db(self._db)
        if not ObjectId.is_valid(document_id):
            raise HTTPException(
                status_code=400,
                detail="document_id 형식이 올바르지 않습니다.",
            )
        doc = await db[DOCUMENTS].find_one({"_id": ObjectId(document_id)})
        if doc is None:
            raise HTTPException(status_code=404, detail="문서를 찾을 수 없습니다.")
        return _to_response(doc)

    async def create_chunk(self, payload: ChunkCreate) -> ChunkResponse:
        db = _require_db(self._db)
        # 부모 문서가 없으면 Chunk를 만들지 않는다. (고아 데이터 방지)
        await self.get(payload.document_id)
        now = datetime.now(UTC)
        chunk = {
            "document_id": ObjectId(payload.document_id),
            "chunk_index": payload.chunk_index,
            "content": payload.content,
            "created_at": now,
        }
        result = await db[CHUNKS].insert_one(chunk)
        chunk["_id"] = result.inserted_id
        return _to_chunk_response(chunk)

    async def list_chunks(self, document_id: str) -> list[ChunkResponse]:
        db = _require_db(self._db)
        if not ObjectId.is_valid(document_id):
            raise HTTPException(
                status_code=400,
                detail="document_id 형식이 올바르지 않습니다.",
            )
        cursor = (
            db[CHUNKS]
            .find({"document_id": ObjectId(document_id)})
            .sort("chunk_index", 1)
        )
        return [_to_chunk_response(chunk) async for chunk in cursor]


    async def delete_chunks(self, document_id: str) -> int:
        # 해당 document_id의 Chunk만 삭제. 없으면 deleted_count=0.
        db = _require_db(self._db)
        if not ObjectId.is_valid(document_id):
            raise HTTPException(
                status_code=400,
                detail="document_id 형식이 올바르지 않습니다.",
            )
        result = await db[CHUNKS].delete_many({"document_id": ObjectId(document_id)})
        return result.deleted_count