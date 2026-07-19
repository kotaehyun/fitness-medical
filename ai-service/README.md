# Fitness Medical AI Service

FastAPI와 MongoDB를 사용하는 RAG 서비스 예정 영역입니다. 아직 AI 코드는 구현하지 않았으며, 직접 학습하면서 작성할 수 있도록 역할만 정의합니다.

## 역할

```text
Spring Boot
  → FastAPI
    → MongoDB 문서 검색
      → LLM 응답 생성
```

MongoDB에는 다음과 같은 비정형 AI 데이터를 저장할 예정입니다.

- RAG 원문 문서
- 문서를 나눈 Chunk
- 문서 출처와 카테고리
- 임베딩 연결 정보
- AI 요청·응답 이력

회원, 건강 기록, 예약, 계정 같은 핵심 서비스 데이터는 MySQL에 저장합니다.

## 예정 폴더 구조

```text
ai-service/
├── app/
│   ├── main.py
│   ├── routers/
│   ├── services/
│   ├── repositories/
│   ├── schemas/
│   └── core/
├── tests/
├── requirements.txt
└── README.md
```

FastAPI와 MongoDB 설치 및 구현은 Spring Boot CRUD 학습 후 별도 단계로 진행합니다.
