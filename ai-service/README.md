# Fitness Medical AI Service

FastAPI + MongoDB + Chroma + 로컬 Ollama 기반 RAG입니다.
문서 저장·Chunk 분할·벡터 검색·근거 기반 답변까지입니다.

> 생활·코칭 안내 검색용입니다. 의료 진단이나 처방을 제공하지 않습니다.

## 역할

```text
Spring Boot (회원·건강 기록, MySQL)
  → FastAPI (이 서비스)
    → MongoDB (원문 Document / Chunk 정본)
    → Chroma (Chunk 임베딩 검색)
    → Ollama (검색 근거로 문장 생성)
```

## 현재 구현

- `POST /documents` 원문 등록
- `POST /documents/{id}/split` 글자 수 기준 분할 후 Mongo 저장 + Chroma 인덱싱
- `GET /documents/{id}/chunks` 저장된 Chunk 조회
- `POST /search` 비슷한 Chunk 검색 (`query`, `n_results`)
- `POST /ask` 검색 근거로 답변 생성 (`answer` + `sources`)
- `GET /health` Mongo / Chroma / Ollama 연결 상태

근거 Chunk가 없으면 LLM을 호출하지 않고 안내 문구만 반환합니다.

## 실행 전 준비

Python **3.12**를 권장합니다. 이 PC의 기본 `python`이 3.14이면 `py -3.12`를 쓰세요.

```bash
cd docker
docker compose up -d mongodb
```

Mongo 기본값: `mongodb://localhost:27017` / DB `fitness_medical_ai`  
포트가 겹치면 `docker-compose.yml`과 `app/core/config.py`의 `mongodb_url`을 같이 맞춥니다.

로컬 Ollama가 `http://127.0.0.1:11434`에서 떠 있어야 `/ask`가 동작합니다.
기본 모델은 `gemma4:e2b`(빠른 로컬 생성)입니다. 다른 모델을 쓰려면:

```bash
# Windows PowerShell
$env:LLM_MODEL="exaone3.5:7.8b"
```

```bash
cd ai-service
py -3.12 -m venv .venv
# Windows
.\.venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

브라우저: http://localhost:8000/docs

## 학습용 호출 순서

1. `POST /documents` 로 안내 문구 원문 등록
2. `POST /documents/{id}/split` 로 조각 + 벡터 저장
3. `POST /search` `{"query": "수면 습관", "n_results": 5}`
4. `POST /ask` `{"query": "잠은 어떻게 자면 좋나요?", "n_results": 5}`

Chroma 데이터는 `ai-service/chroma_data/`에 남고 git에는 올리지 않습니다.
첫 `split`/`search` 때 로컬 임베딩 모델(all-MiniLM-L6-v2)을 받아 시간이 걸릴 수 있습니다.
`/ask`는 Ollama 모델 크기에 따라 수 초~수십 초 걸릴 수 있습니다.
