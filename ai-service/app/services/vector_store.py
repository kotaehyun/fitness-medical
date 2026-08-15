"""
[공부/면접] Chunk 임베딩 저장·검색. Mongo Repository와 짝을 이룬다.

Q. 왜 document_id로 지우고 다시 넣나?
A. split을 다시 하면 Chunk 개수·내용이 바뀐다.
   예전 벡터가 남으면 검색에 낡은 조각이 섞인다.

Q. ids에 Mongo Chunk id를 쓰는 이유?
A. 나중에 Mongo 원문과 검색 결과를 이어 보려면 같은 id가 편하다.

Q. distance는?
A. 쿼리와 Chunk 사이 거리. cosine 공간에서는 작을수록 비슷하다.
"""

from fastapi import HTTPException
from chromadb.api.models.Collection import Collection

from app.schemas.document import ChunkResponse, SearchHit


def _require_collection(collection: Collection | None) -> Collection:
    if collection is None:
        raise HTTPException(
            status_code=503,
            detail="Chroma에 연결되지 않았습니다.",
        )
    return collection


class VectorStore:
    def __init__(self, collection: Collection | None) -> None:
        self._collection = collection

    def replace_document_chunks(
        self,
        document_id: str,
        chunks: list[ChunkResponse],
        *,
        title: str,
    ) -> None:
        collection = _require_collection(self._collection)
        collection.delete(where={"document_id": document_id})
        if not chunks:
            return
        collection.upsert(
            ids=[chunk.id for chunk in chunks],
            documents=[chunk.content for chunk in chunks],
            metadatas=[
                {
                    "document_id": chunk.document_id,
                    "chunk_index": chunk.chunk_index,
                    "title": title,
                }
                for chunk in chunks
            ],
        )

    def query(self, text: str, n_results: int = 5) -> list[SearchHit]:
        collection = _require_collection(self._collection)
        count = collection.count()
        if count == 0:
            return []

        result = collection.query(
            query_texts=[text],
            n_results=min(n_results, count),
        )
        ids = (result.get("ids") or [[]])[0]
        documents = (result.get("documents") or [[]])[0]
        metadatas = (result.get("metadatas") or [[]])[0]
        distances = (result.get("distances") or [[]])[0]

        hits: list[SearchHit] = []
        for index, chunk_id in enumerate(ids):
            metadata = metadatas[index] if index < len(metadatas) else {}
            distance = distances[index] if index < len(distances) else None
            hits.append(
                SearchHit(
                    chunk_id=chunk_id,
                    document_id=str(metadata.get("document_id", "")),
                    chunk_index=int(metadata.get("chunk_index", 0)),
                    title=str(metadata.get("title", "")),
                    content=documents[index] if index < len(documents) else "",
                    distance=float(distance) if distance is not None else None,
                )
            )
        return hits
