# Fitness Medical

회원의 건강 데이터와 운동 기록을 의료 전문가 및 트레이너가 함께 관리하는 시연용 헬스케어 SaaS 프로젝트입니다.

> 본 서비스의 인물과 건강 데이터는 시연용 가상 데이터이며, 의료 진단이나 처방을 제공하지 않습니다.

## 프로젝트 구성

```text
Fitness_medical/
├── frontend/       # React + Vite 웹 애플리케이션
├── backend/        # Spring Boot REST API 기본 구조
├── ai-service/     # FastAPI 기반 AI 서비스 (추후 구현)
├── docker/         # MySQL 등 로컬 인프라 설정
└── README.md       # 전체 프로젝트 안내
```

현재 프론트엔드와 Spring Boot 백엔드 기본 API가 구현되어 있습니다. 프론트엔드는 아직 mock service를 기본으로 사용하므로 백엔드 없이도 실행할 수 있습니다.

## 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

자세한 프론트엔드 구조는 [`frontend/README.md`](./frontend/README.md)를 확인하세요.

## 백엔드 실행

Java 21과 Gradle을 설치한 환경에서 실행합니다.

```bash
cd backend
gradle bootRun
```

자세한 API와 설정은 [`backend/README.md`](./backend/README.md)를 확인하세요.

## 예정 기술 스택

### Frontend

- React, JavaScript, Vite
- React Router, TanStack Query
- Recharts, Lucide React
- CSS

### Backend

- Java 21
- Spring Boot, Spring Data JPA, Spring Security
- MySQL 8.4 (Docker, 포트 3307)

### AI Service

- Python, FastAPI
- LLM/RAG
- MongoDB 기반 RAG 문서·Chunk 저장
