# 2026-08-12 — Chroma 검색 + `/ask`(Ollama) + Spring base-url

## 결과

전날 나눈 Chunk를 **Chroma에 넣고 벡터 검색 API**를 붙임. FastAPI `/ask`로 Ollama에 근거 기반 답을 생성. Spring에 AI 서비스 `base-url` 설정만 추가 (실제 `/api/ai/ask` 중계·UI·Guard는 **08-13**).

LangChain / LangGraph 미사용. 진단·처방 아님.

## 작업한 내용

- Chunk → Chroma upsert + similarity 검색 (`3222ad6`)
- `/ask` + Ollama, 검색 Chunk를 근거로 답 생성 (`e7a9141`)
- Spring `fitness.ai.base-url` / 환경변수 `AI_BASE_URL` (`d520d08`)  
  기본 `http://127.0.0.1:8000`

## 설계 결정

- 임베딩·검색은 FastAPI. Spring은 나중에 중계만 (08-13).
- 모델 이름은 코드 하드코딩보다 `LLM_MODEL` 환경변수. Mac은 `gemma4:e4b`, 문서 기본 `gemma4:e2b`.

## 진행 중 겪은 문제와 해결

1. **Ollama 모델이 머신마다 다름 → 로드 실패**  
   RTX/Mac 기본 모델이 다름.  
   → `LLM_MODEL`로 덮어씀. Mac 예: `export LLM_MODEL="gemma4:e4b"`

2. **Chroma에 안 넣고 `/ask`만 치면 근거가 없음**  
   → 먼저 ingest(Chunk 적재) 후 검색. 빈 컬렉션이면 답 품질이 없음.

3. **Spring만 띄우고 FastAPI를 안 띄움**  
   이날은 base-url 설정만이라 중계 호출은 아직. 08-13에 중계하면 연결 거부 → 503으로 처리.

4. **관련 없는 Chunk가 섞이는 문제**  
   이날은 top-k 검색까지. 거리 컷(`MAX_DISTANCE=0.55`)은 **08-13**.

## 실행 (이날 기준)

```bash
docker compose up -d mongodb
cd ai-service && uvicorn app.main:app --reload --port 8000
# Ollama 실행 + LLM_MODEL 확인
```

Spring `AI_BASE_URL`은 `application.yml`에만 넣어 둔 상태. 프론트 연동은 다음날.

## 커밋

- `3222ad6` feat: split한 Chunk를 Chroma에 넣고 벡터 검색 API를 붙인다
- `e7a9141` feat(ai-rag): /ask(Ollama)로 근거 기반 답변 생성까지 연결
- `d520d08` feat: Spring에 FastAPI AI 서비스 base-url 설정을 추가한다

## 다음에 할 일 (다음날 08-13)

- [x] `POST /api/ai/ask` 중계 + 회원 생활안내 UI
- [x] Guardrail, Router/Retriever/Writer 분리, MAX_DISTANCE
