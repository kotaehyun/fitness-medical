# Fitness Medical 프론트엔드·Spring Boot API 연동 보고서

## 작업 일자

2026-08-06

## 작업 목적

React + Vite 프론트엔드를 `http://localhost:8081/api` Spring Boot API와 연결하고, 세션 로그인과 역할별 화면 접근을 실제 서비스 흐름으로 구성했습니다.

## 주요 작업 내용

### 1. API 환경 설정

- `frontend/.env.local` 생성
- `VITE_API_URL=http://localhost:8081/api` 설정
- `VITE_USE_MOCK=false` 설정으로 실제 API 사용
- Mock 서비스는 환경변수로 다시 전환할 수 있도록 유지

### 2. 공통 API 요청과 세션 처리

- `apiService.js`의 기본 API 주소를 `8081`로 변경
- 모든 `fetch` 요청에 `credentials: 'include'` 적용
- `JSESSIONID` 세션 유지를 위한 로그인 및 현재 계정 조회 메서드 추가
- 회원, 건강 기록, 피드백 DTO를 프론트 화면 모델로 변환

### 3. 실제 로그인 연결

- `POST /api/auth/login` 호출
- 요청 본문에 `loginId`, `password` 전송
- 로그인 응답의 `role`에 따른 이동
  - `MEMBER` → `/member`
  - `PROFESSIONAL` → `/professional`
- 역할 응답이 없을 경우 `/api/auth/me`로 인증 정보 확인
- 로그인 중 상태와 한국어 오류 메시지 표시
- 기존 회원·전문가 데모 버튼은 별도 데모 역할로 유지

### 4. 인증 및 권한별 라우팅

- `ProtectedRoute` 추가
- `/api/auth/me`로 세션 인증 확인
- 비로그인 사용자는 `/login`으로 이동
- 역할이 맞지 않는 화면 접근 시 자신의 역할 화면으로 이동
- 인증 확인 중 loading 상태 표시
- 데모 모드는 `sessionStorage`의 `fitness-demo-role`로 실제 계정 로그인과 분리

### 5. 실제 API 데이터 표시

- 회원 대시보드와 전문가 회원 상세 화면에서 최신 건강 기록 사용
- 혈압, 혈당, 체중, 걸음 수를 하드코딩 값 대신 API 데이터로 표시
- 기록이 없는 경우 `—`와 안내 문구 표시
- 상세 화면의 회원 기록·피드백 조회 ID 하드코딩 제거
- 전문가 회원 목록 empty 상태 추가

### 6. 피드백 등록 연결

- `POST /api/members/{memberId}/feedback` 호출 추가
- 작성자, 역할, 내용을 백엔드 DTO 형식으로 전송
- 등록 중 버튼 상태 표시
- 등록 성공 시 입력 폼 초기화 및 피드백 목록 갱신
- 등록 실패 시 오류 메시지 표시
- Mock 서비스에도 동일한 메서드 추가

### 7. CORS 수정

문제 원인은 백엔드 CORS가 `localhost:5174`만 허용하고, 세션 쿠키 허용 설정이 없었던 것입니다.

- `localhost:5173`, `localhost:5174` 허용
- `allowCredentials(true)` 추가
- Spring Security CORS 활성화
- CRUD 도메인 로직은 수정하지 않고 웹 연동 설정만 수정

## 변경 주요 파일

- `frontend/.env.local`
- `frontend/src/services/apiService.js`
- `frontend/src/services/mockService.js`
- `frontend/src/pages/auth/LoginPage.jsx`
- `frontend/src/app/router.jsx`
- `frontend/src/pages/member/MemberDashboard.jsx`
- `frontend/src/pages/professional/MemberDetailPage.jsx`
- `frontend/src/pages/professional/ProfessionalDashboard.jsx`
- `frontend/src/vite-env.d.ts`
- `backend/src/main/java/com/fitnessmedical/config/WebConfig.java`
- `backend/src/main/java/com/fitnessmedical/config/SecurityConfig.java`

## 문서 보완

- 루트 `README.md`에 실제 API 연동 상태 반영
- `frontend/README.md`에 환경변수, 실행 순서, CORS, 세션 쿠키 안내 추가
- `backend/README.md`에 프론트엔드 연동 조건 추가

## 실행 방법

### 백엔드

```bash
cd backend
SPRING_PROFILES_ACTIVE=mysql SERVER_PORT=8081 ./gradlew bootRun
```

### 프론트엔드

```bash
cd frontend
npm run dev
```

환경변수 변경 후에는 Vite 개발 서버를 재시작해야 합니다.

## 검증 결과

- `backend: ./gradlew compileJava` 성공
- `frontend: npm run build` 성공
- Vite 번들 크기 경고만 발생했으며 빌드 오류는 없음

## 참고 사항

- 데모 버튼은 화면 체험용이며 실제 계정 인증과 구분됩니다.
- 실제 API 모드에서는 백엔드, MySQL, CORS 설정이 모두 실행되어야 합니다.
- 프론트에서 API 요청 시 세션 유지를 위해 `credentials: 'include'`가 필요합니다.

## 후속 프론트 구현

- `/professional/members` 회원관리 화면에 목표·진행률 수정 모달 추가
- `PUT /api/members/{memberId}` 호출 및 성공 후 회원 목록 갱신
- `DELETE /api/members/{memberId}` 호출 전 삭제 확인 추가
- `204 No Content` 응답을 공통 API 요청 함수에서 처리
- API 모드와 Mock 모드 모두 회원 수정·삭제 메서드 지원
