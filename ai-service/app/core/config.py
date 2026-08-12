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

Q. LLM은 왜 로컬 Ollama인가?
A. API 키 없이 RTX 노트북에서 학습하기 위함이다.
   모델 이름은 LLM_MODEL 환경변수로 바꾼다. 키는 로그에 남기지 않는다.
"""

import os
from pathlib import Path

AI_SERVICE_ROOT = Path(__file__).resolve().parents[2]


class Settings:
    app_name: str = "Fitness Medical AI Service"
    mongodb_url: str = "mongodb://localhost:27017"
    mongodb_db: str = "fitness_medical_ai"
    chroma_persist_directory: str = str(AI_SERVICE_ROOT / "chroma_data")
    chroma_collection: str = "fitness_chunks"
    # OLLAMA_HOST=0.0.0.0 은 바인드 주소라 URL로 쓰지 않는다.
    ollama_base_url: str = os.getenv("OLLAMA_BASE_URL", "http://127.0.0.1:11434")
    llm_model: str = os.getenv("LLM_MODEL", "gemma4:e2b")
    llm_timeout_seconds: float = float(os.getenv("LLM_TIMEOUT_SECONDS", "120"))


settings = Settings()
