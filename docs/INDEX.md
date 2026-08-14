# docs

Fitness Medical 프로젝트의 학습/진행 기록용 문서 폴더입니다. `README.md`, `backend/README.md`, `backend/STUDY_TASKS.md`, `backend/erd/`와 별도로, **작업하면서 남기는 기록**을 모읍니다.

코드를 추가·수정한 세션에서는 **그날 날짜의 `progress/`에 작업 내용 + 오류를 쓰고, 이 INDEX에도 링크를 넣습니다.** 여러 날을 한 파일에 묶지 않습니다. 프론트+백엔드를 한 흐름으로 바꾼 날은 `report/`도 같이 남깁니다.

## 카테고리

- [`progress/`](./progress) — **날짜별** 진행 로그 (파일명 `YYYY-MM-DD-주제.md`)
- [`decisions/`](./decisions) — 설계 결정
- [`troubleshooting/`](./troubleshooting) — 삽질·해결
- [`study/`](./study) — 코드 리뷰·학습 노트
- [`report/`](./report) — 프론트+백엔드 작업 보고서

### progress

| 날짜 | 문서 |
|---|---|
| 2026-08-01 | [1단계: 회원 등록](./progress/2026-08-01-backend-step1-회원등록.md) |
| 2026-08-02 | [2단계: 회원 수정·삭제](./progress/2026-08-02-backend-step2-회원수정삭제.md) |
| 2026-08-04 | [3단계: 전문가 피드백 등록](./progress/2026-08-04-backend-step3-피드백등록.md) |
| 2026-08-06 | [4단계: Session 인증 + 프론트 API 연동](./progress/2026-08-06-backend-step4-session-auth-frontend-연동.md) |
| 2026-08-08 | [Account–Member 1:1](./progress/2026-08-08-account-member-1대1.md) |
| 2026-08-10 | [MySQL 포트 3308 안내](./progress/2026-08-10-mysql-포트-3308.md) |
| 2026-08-11 | [RAG: Mongo·Chunk·Chroma 준비](./progress/2026-08-11-rag-mongo-chunk-chroma.md) |
| 2026-08-12 | [RAG: Chroma 검색 + /ask(Ollama) + Spring base-url](./progress/2026-08-12-rag-chroma-ask-ollama.md) |
| 2026-08-13 | [RAG: 중계·Guard·Router/Retriever/Writer](./progress/2026-08-13-rag-가드레일-멀티에이전트.md) |
| 2026-08-13 | [회원 API 소유권 + 전문직 미인증 가입](./progress/2026-08-13-회원api-소유권-전문직가입.md) |
| 2026-08-13 | [ADMIN 승인·세션 UX·가입 현황](./progress/2026-08-13-admin-세션ux-가입현황.md) |
| 2026-08-14 | [AI /ask 로그인 필수 + 파비콘](./progress/2026-08-14-ai-ask-인증-파비콘.md) |

### decisions

- [MemberCreateRequest 필드 설계](./decisions/member-create-request-필드-설계.md)

### troubleshooting

- [2026-08-01 로컬 8080 Oracle XML DB 충돌](./troubleshooting/2026-08-01-local-port-8080-oracle-충돌.md)

### study

- [STUDY_TASKS 4단계 Session 인증 리뷰](./study/codeReview/step4-session-auth-review.md) (Entity/Repository만, 구현 전)

### report

- [2026-08-06 프론트엔드·Spring API 연동](./report/2026-08-06-frontend-spring-api-연동.md)
- [2026-08-13 관리자 승인·세션 UX·가입 현황](./report/2026-08-13-admin-auth-session-가입현황.md)

## 작성 규칙

- 파일명: `YYYY-MM-DD-주제.md`. **한 파일에 여러 날짜를 넣지 않는다.** 같은 날 주제가 갈리면 파일을 나눈다.
- 새 문서 추가 시 이 INDEX 해당 날짜 행에 링크를 넣는다.
- 학원 단계·기능 → `progress/` (파일·설계·오류). 풀스택 한 흐름 → `report/` (+ 그날 progress).
