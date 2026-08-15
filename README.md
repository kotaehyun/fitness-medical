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

## 역할별 시연

시드 계정 아이디: `member01` / `trainer01` / `doctor01` / `admin01`.
비밀번호는 [`backend/README.md`](./backend/README.md)만 본다.

| 역할 | 화면 | 확인 |
|------|------|------|
| MEMBER | `/member` → 기록, 안내, 피드백, 메시지 | 본인 데이터만. 담당 전문의·트레이너와 텍스트 대화 |
| PROFESSIONAL (미인증) | 공개 가입 직후 | 회원 목록·피드백·메시지는 403 |
| PROFESSIONAL (인증) | `/professional` → 회원 관리, 메시지 | 목록에 담당 표시. 맡은 회원·같이 맡은 전문가와만 대화 |
| ADMIN | `/admin` | 전문직 승인/해제·가입 현황. 건강 API·메시지는 이 역할로 열지 않음 |

핵심: 세션 로그인, 소유권, 전문직 승인, 담당 연결 텍스트 채팅, 생활 안내 RAG.  
실험·이후: 예약, 설정, CSRF, Flyway, 웨어러블, 모델 학습, 이미지·영상, 담당 변경 UI. 진단·처방 API가 아니다.
