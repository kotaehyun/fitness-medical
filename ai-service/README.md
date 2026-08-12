# Fitness Medical AI Service

FastAPI + MongoDB + Chroma 기반 RAG **1단계**입니다.
지금은 문서 저장·Chunk 분할·벡터 검색까지입니다. LLM 답변 생성은 아직 없습니다.

> 생활·코칭 안내 검색용입니다. 의료 진단이나 처방을 제공하지 않습니다.

## 역할

```text
Spring Boot (회원·건강 기록, MySQL)
  → FastAPI (이 서비스)
    → MongoDB (원문 Document / Chunk 정본)
    → Chroma (Chunk 임베딩 검색)
      → (다음) LLM 응답 생성
```

## 현재 구현

- `POST /documents` 원문 등록
- `POST /documents/{id}/split` 글자 수 기준 분할 후 Mongo 저장 + Chroma 인덱싱
- `GET /documents/{id}/chunks` 저장된 Chunk 조회
- `POST /search` 비슷한 Chunk 검색 (`query`, `n_results`)
- `GET /health` Mongo / Chroma 연결 상태

## 실행 전 준비

Python **3.12**를 권장합니다. 이 PC의 기본 `python`이 3.14이면 `py -3.12`를 쓰세요.

```bash
cd docker
docker compose up -d mongodb
```

Mongo 기본값: `mongodb://localhost:27017` / DB `fitness_medical_ai`  
포트가 겹치면 `docker-compose.yml`과 `app/core/config.py`의 `mongodb_url`을 같이 맞춥니다.

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

Chroma 데이터는 `ai-service/chroma_data/`에 남고 git에는 올리지 않습니다.
첫 `split`/`search` 때 로컬 임베딩 모델(all-MiniLM-L6-v2)을 받아 시간이 걸릴 수 있습니다.
