"""
[공부/면접] 헬스체크 라우터.

Q. 왜 status 200이어도 mongodb가 disconnected일 수 있나?
A. 프로세스(앱) 생존과 DB 연결은 다른 문제다.
   앱은 떠 있고 Mongo만 꺼진 상태를 /health로 구분한다.
   Document API는 그때 503을 준다.
"""

from fastapi import APIRouter

from app.core.database import get_database

router = APIRouter(tags=["health"])


@router.get("/health")
def health():
    return {
        "status": "ok",
        "mongodb": "connected" if get_database() is not None else "disconnected",
    }
