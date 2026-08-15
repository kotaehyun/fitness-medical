"""
[공부/면접] MongoDB 연결 수명주기.

Q. motor는 뭔가?
A. MongoDB의 비동기 Python 드라이버. FastAPI가 async라 pymongo 동기 호출보다 맞다.

Q. ping 실패 시 앱을 죽이지 않는 이유는?
A. 로컬에서 Mongo가 꺼져 있어도 /health로 상태를 볼 수 있게 한다.
   Document API는 get_database()가 None이면 503을 반환한다.

Q. global _client/_db 는?
A. 프로세스당 클라이언트 1개를 재사용한다. 요청마다 새로 만들면 느리다.
"""

from contextlib import asynccontextmanager

from fastapi import FastAPI
from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase

from app.core.chroma import close_chroma, init_chroma
from app.core.config import settings

_client: AsyncIOMotorClient | None = None
_db: AsyncIOMotorDatabase | None = None


def get_database() -> AsyncIOMotorDatabase | None:
    return _db


@asynccontextmanager
async def lifespan(app: FastAPI):
    global _client, _db
    try:
        _client = AsyncIOMotorClient(
            settings.mongodb_url,
            serverSelectionTimeoutMS=2000,
        )
        # ping이 성공해야 실제로 연결된 것이다. 클라이언트 생성만으로는 부족하다.
        await _client.admin.command("ping")
        _db = _client[settings.mongodb_db]
    except Exception:
        _client = None
        _db = None

    init_chroma()
    yield
    close_chroma()

    if _client is not None:
        _client.close()
        _client = None
        _db = None
