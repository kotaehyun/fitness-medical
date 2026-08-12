"""
[공부/면접] 앱 설정.

Q. 왜 URL/DB 이름을 코드에 하드코딩하나?
A. 지금은 로컬 학습용 기본값이다. 이후 pydantic-settings + .env로 옮긴다.
   비밀번호·API 키는 절대 로그에 출력하지 않는다.

Q. MySQL과 MongoDB를 나누는 이유는?
A. 회원·계정은 정형(MySQL), RAG 원문·Chunk는 비정형(MongoDB).

Q. Chroma 경로를 상대경로로 두지 않는 이유는?
A. uvicorn 실행 위치에 따라 ./chroma_data 위치가 달라진다.
   이 파일 기준으로 ai-service/chroma_data 를 고정한다.
"""

from pathlib import Path

AI_SERVICE_ROOT = Path(__file__).resolve().parents[2]


class Settings:
    app_name: str = "Fitness Medical AI Service"
    mongodb_url: str = "mongodb://localhost:27017"
    mongodb_db: str = "fitness_medical_ai"
    chroma_persist_directory: str = str(AI_SERVICE_ROOT / "chroma_data")
    chroma_collection: str = "fitness_chunks"


settings = Settings()
