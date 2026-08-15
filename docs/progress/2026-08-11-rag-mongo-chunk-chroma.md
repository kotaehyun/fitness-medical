# 2026-08-11 — FastAPI Mongo 문서 + Chunk + Chroma 준비

## 결과

AI 서비스에 Mongo에 쌓인 문서를 읽고 **Chunk로 분할**하는 코드를 붙임. Chroma(벡터 DB) 의존성까지 준비. 아직 `/ask`·Ollama·Spring 중계는 없음 (다음날~모레).

Fine-tuning 없음. 진단·처방 API 아님.

## 작업한 내용

- `ai-service`: Mongo 문서 조회
- 문장/단락 단위 Chunk 분할 (`test_chunking.py` 등)
- Chroma 패키지·설정 준비 (적재·검색 API는 08-12)

## 설계 결정

- 원문을 통째로 임베딩하지 않고 Chunk로 나눈 뒤 검색. RAG 기본.
- 벡터 저장은 Chroma. Mongo는 원문/메타 보관.

## 진행 중 겪은 문제와 해결

1. **문서를 너무 크게 자르면 검색이 거칠고, 너무 잘게 자르면 맥락이 깨짐**  
   → 학습용 고정 길이/겹침 Chunk. 이후 Retriever가 거리로 한 번 더 걸러냄 (08-13 `MAX_DISTANCE`).

2. **Chroma를 바로 `/ask`에 붙이면 범위가 커짐**  
   → 이날은 분할 + 의존성만. 검색 API는 08-12.

3. **Docker Mongo가 안 떠 있으면 문서 조회 실패**  
   → `docker compose up -d mongodb` 후 FastAPI 실행.

## 커밋

- `9bdb45c` feat: FastAPI에 Mongo 문서·Chunk 분할을 붙이고 Chroma 의존성을 준비한다

## 다음에 할 일 (다음날)

- [x] Chunk를 Chroma에 넣고 검색 API (08-12)
- [x] `/ask` + Ollama (08-12)
- [x] Spring 중계·Guard·역할 분리 (08-13)
