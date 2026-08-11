"""
[공부/면접] FastAPI 진입점.

Q. Spring Boot의 @SpringBootApplication과 비슷한 파일은?
A. 이 main.py. FastAPI()로 앱을 만들고, router를 include한다.

Q. lifespan은?
A. 앱 시작/종료 훅. MongoDB connect/close를 여기서 연결한다.
   Spring의 DataSource 초기화와 비슷한 위치다.

주의: 의료 진단·처방처럼 단정하는 API를 만들지 않는다.
"""

from fastapi import FastAPI

from app.core.database import lifespan
from app.routers import documents, health

app = FastAPI(title="Fitness Medical AI Service", lifespan=lifespan)
app.include_router(health.router)
app.include_router(documents.router)


@app.get("/")
def root():
    return {
        "message": "Fitness Medical AI Service",
        "docs": "/docs",
        "health": "/health",
    }
