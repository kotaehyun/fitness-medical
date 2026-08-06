# Fitness Medical Frontend

Fitness Medical의 React 기반 프론트엔드입니다. 백엔드 API 없이도 mock service와 더미데이터로 실행됩니다.

## 실행 방법

```bash
npm install
npm run dev
```

프로덕션 빌드는 `npm run build`, 코드 정리는 `npm run format`으로 실행합니다.

코드의 데이터 흐름과 면접·발표 설명은 [`FRONTEND_CODE_GUIDE.md`](./FRONTEND_CODE_GUIDE.md)를 먼저 읽어보세요. 핵심 소스에는 `[발표 핵심]` 한국어 주석을 추가했습니다.

## 폴더 구조

```text
src/
├── app/          # Router, 전역 Provider
├── assets/       # 웹에서 사용하는 이미지
├── components/   # 여러 화면에서 다시 사용하는 UI
├── data/mock/    # 시연용 더미데이터
├── hooks/        # TanStack Query 데이터 조회 훅
├── pages/        # URL별 페이지 컴포넌트
├── services/     # UI와 분리된 데이터 접근 코드
├── styles/       # 공통 CSS와 반응형 스타일
└── main.jsx      # React 시작점
```

## 데이터 흐름

```text
Page → Query Hook → Health Service → API Service 또는 Mock Service
```

페이지 컴포넌트는 데이터를 직접 만들지 않습니다. `VITE_USE_MOCK` 값에 따라 실제 Spring Boot API 또는 mock service를 선택합니다.

실제 API를 사용하려면 `frontend/.env.local`을 만들고 다음 값을 설정합니다.

```env
VITE_USE_MOCK=false
VITE_API_URL=http://localhost:8081/api
```

환경변수는 Vite 시작 시 읽으므로 `.env.local`을 변경한 뒤에는 프론트 개발 서버를 재시작해야 합니다.

백엔드는 먼저 실행합니다.

```bash
cd backend
SPRING_PROFILES_ACTIVE=mysql SERVER_PORT=8081 ./gradlew bootRun
```

프론트 개발 서버의 `localhost:5173`과 `localhost:5174` 요청은 백엔드 CORS 설정에서 허용됩니다. API 요청은 `credentials: 'include'`를 사용해 `JSESSIONID` 세션을 유지합니다.

## 주요 URL

- `/` 랜딩 페이지
- `/login` 체험 로그인
- `/member` 회원 대시보드
- `/member/records` 건강 기록
- `/professional` 전문가 대시보드
- `/professional/members/m1` 전문가용 회원 상세

## 코드 작성 기준

- JSX와 JavaScript 사용
- 컴포넌트는 한 가지 역할 중심으로 작성
- 데이터 접근은 `services`와 `hooks`로 분리
- 반복 UI는 `components`로 분리
- 기본 React Hook과 TanStack Query 사용
- 데스크톱, 태블릿, 모바일 반응형 지원
