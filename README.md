# Fitness Medical

회원의 건강 기록과 전문가 피드백을 역할별로 나누어 보는 시연용 건강관리 플랫폼입니다.
React · Spring Boot · FastAPI(RAG)로 구성되어 있습니다.

> 인물과 건강 수치는 시연용 가상 데이터입니다. 의료 진단이나 처방을 하지 않습니다.

## 구성

```text
Fitness_medical/
├── frontend/     # React + Vite
├── backend/      # Spring Boot REST API
├── ai-service/   # FastAPI RAG (Chroma · Ollama)
├── docker/       # MySQL · MongoDB
└── docs/         # 진행 기록
```

실행·환경변수·API 상세는 **각 폴더 README**에 있습니다.

| 폴더 | 설명 |
|------|------|
| [`frontend/README.md`](./frontend/README.md) | 화면, Vite, `.env.local`, mock/API |
| [`backend/README.md`](./backend/README.md) | API, 세션 인증, 데모 계정, H2/MySQL |
| [`ai-service/README.md`](./ai-service/README.md) | 문서·검색·`/ask`, Mongo·Ollama |
| [`docker/README.md`](./docker/README.md) | 로컬 DB 컨테이너 |
| [`backend/erd/fitness-medical-erd.md`](./backend/erd/fitness-medical-erd.md) | ERD |

이 기기에서 백엔드 포트는 **8081**입니다. 8080은 Oracle XML DB와 겹칩니다.

## 한 줄 요약

역할별 세션 로그인, Account–Member 소유권, 전문직 관리자 승인, 건강 기록·피드백,
Spring `POST /api/ai/ask`(로그인 필수)로 생활 안내 RAG를 붙인 학습·시연용 프로젝트입니다.
