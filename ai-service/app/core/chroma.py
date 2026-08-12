"""
[공부/면접] Chroma 로컬 벡터 저장소.

Q. MongoDB와 Chroma를 같이 쓰는 이유는?
A. Mongo는 원문·Chunk 원본(정본), Chroma는 검색용 임베딩.
   원문을 다시 읽고 싶을 때는 Mongo, 비슷한 문장을 찾을 때는 Chroma.

Q. PersistentClient는?
A. 메모리만이 아니라 디스크(chroma_data/)에 저장한다.
   서버를 재시작해도 인덱스가 남는다. git에는 올리지 않는다.

Q. 기본 임베딩 모델은?
A. Chroma DefaultEmbeddingFunction(로컬 ONNX). API 키가 필요 없다.
   첫 실행 때 모델을 받아서 시간이 걸릴 수 있다.

Q. Chroma는 동기 API인데 FastAPI는 async?
A. 호출부는 asyncio.to_thread로 감싸 이벤트 루프를 막지 않는다.
"""

from pathlib import Path

import chromadb
from chromadb.api import ClientAPI
from chromadb.api.models.Collection import Collection

from app.core.config import settings

_client: ClientAPI | None = None
_collection: Collection | None = None


def get_collection() -> Collection | None:
    return _collection


def init_chroma() -> None:
    global _client, _collection
    try:
        persist = Path(settings.chroma_persist_directory)
        persist.mkdir(parents=True, exist_ok=True)
        _client = chromadb.PersistentClient(path=str(persist))
        _collection = _client.get_or_create_collection(
            name=settings.chroma_collection,
            metadata={"hnsw:space": "cosine"},
        )
    except Exception:
        _client = None
        _collection = None


def close_chroma() -> None:
    global _client, _collection
    _client = None
    _collection = None
