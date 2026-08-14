# Fitness Medical 관리자 승인·세션 UX·가입 현황 보고서

## 작업 일자

2026-08-13

## 작업 목적

공개 회원가입 전문가는 면허/자격 **형식만** 확인하고 `professionalVerified=false`로 두었습니다. 승인할 관리자가 없으면 가입이 막다른 길이 되므로, **슈퍼계정(ADMIN)** 으로 승인/해제하고 일반 회원 가입 현황까지 한 화면에서 보게 했습니다. 같은 흐름을 로컬에서 테스트할 수 있도록 로그인 세션 UX(로그아웃, 역할 홈, 401/403, 약관)도 맞췄습니다.

## 배경

- 이전 보안 수정(`a90684c`)으로 공개 가입 PROFESSIONAL은 항상 미인증이고, 미인증 전문가는 회원 API·피드백을 쓸 수 없습니다.
- 시드 `trainer01`/`doctor01`만 즉시 전문가 화면을 쓸 수 있었습니다.
- 로그인 후 랜딩이 비로그인처럼 보이거나, `/api/auth/me`가 403으로 찍히거나, 미인증 전문가가 `/api/members`를 호출해 콘솔 403이 반복되는 UX 문제가 있었습니다.

## 주요 작업 내용

### 1. 로그인 세션 UX

- `useAuthSession.js`로 데모(`sessionStorage` `fitness-demo-role`)와 실로그인(`/api/auth/me`)을 한 규칙으로 판단
- 로그인 성공 시 React Query `['auth','me']`에 `setQueryData` → ProtectedRoute가 401 캐시로 튕기지 않음
- `POST /api/auth/logout`으로 SecurityContext + HTTP 세션 무효화 (204)
- 로그인된 `/`는 마케팅 랜딩이 아니라 역할 홈으로 이동
  - `MEMBER` → `/member`
  - `PROFESSIONAL` → `/professional`
  - `ADMIN` → `/admin`
- AppShell·로그인 화면에 실제 `displayName`과 로그아웃 버튼
- 로고 클릭도 비로그인은 `/`, 로그인은 역할 홈

### 2. 미인증 401 / 권한 없음 403

- Spring Security 기본은 미인증도 403이어서 브라우저 콘솔에 `/api/auth/me` 403이 찍혔음
- `authenticationEntryPoint` → 401 JSON `"로그인이 필요합니다."`
- `accessDeniedHandler` → 403 JSON `"접근 권한이 없습니다."`
- 프론트 `getCurrentAccount()`는 401/403이면 `null`(로그아웃으로 처리)

### 3. 개인정보 약관 분리

- `/privacy/member`, `/privacy/professional` 별도 페이지
- 회원가입 시 역할별 약관 동의 체크
- AppShell 하단 링크도 역할에 맞게 분기

### 4. 미인증 전문가 화면

- 공개 가입 전문가가 `/api/members`를 치면 403이 맞음
- 대시보드·회원관리에서 `professionalVerified === false`이면 목록 API를 호출하지 않음 (`useMembers({ enabled })`)
- 대기 안내: 관리자(`admin01`) 승인 후 이용
- `useMembers` 안에서 `useAuthSession`을 호출하지 않음 → hooks 순서 오류 방지

### 5. 슈퍼계정(ADMIN)

- `AccountRole.ADMIN` 추가. 공개 가입으로 ADMIN을 만들 수 없음
- 시드 `admin01` / `password123`
- `ADMIN`은 `member_id` null. 회원 API는 쓰지 않음(승인만)
- Security: `/api/admin/**` `hasRole("ADMIN")` — `/api/**` permitAll보다 **앞에** 둠

### 6. 관리자 종합 현황

전문직 승인만 보면 가입 현황을 모르므로 한 화면에 모았습니다.

| Method | URL | 설명 |
|---|---|---|
| GET | `/api/admin/members` | 일반 회원 가입 현황 |
| GET | `/api/admin/professionals` | 전문직 목록(미인증 우선) |
| POST | `/api/admin/professionals/{id}/verify` | 인증 승인 |
| POST | `/api/admin/professionals/{id}/revoke` | 인증 해제 |

- 회원 현황: loginId, displayName, 성별, 나이, 목표, 상태(양호/주의/확인 필요)
- password·키·체중·면허번호는 응답/화면에 넣지 않음
- 요약 카드: 일반 회원 수 / 전문직 수 / 인증 대기 수
- 프론트 `/admin` → `AdminVerificationPage`

### 7. ERD·README

- `accounts.role`: MEMBER, PROFESSIONAL, ADMIN
- PROFESSIONAL·ADMIN은 `member_id` null
- `backend/README.md` 데모 계정·관리자 API 표 갱신

## 로컬 데모 계정

비밀번호 모두 `password123`.

| loginId | 역할 |
|---|---|
| `member01` | 회원(김순자) |
| `trainer01` | 트레이너(시드 인증 완료) |
| `doctor01` | 전문의(시드 인증 완료) |
| `admin01` | 관리자(가입 현황 + 승인/해제) |

공개 가입 전문의 예시 면허: `987654` (시드 `123456`과 겹치면 409).  
트레이너 예시 자격: `SP22005678` (시드 `SP21001234`와 겹치면 409).

## 테스트 흐름

1. 전문가로 회원가입 → 로그인하면 “인증 대기”
2. `admin01` 로그인 → `/admin`에서 해당 계정 **승인**
3. 전문가로 다시 로그인 → 회원 목록 사용 가능
4. 관리자가 **해제**하면 다시 대기
5. `/admin` 상단에서 일반 회원(`member01` 및 공개 가입 MEMBER) 현황 확인

## 변경 주요 파일

### 백엔드

- `entity/AccountRole.java`, `entity/Account.java` — ADMIN, `setProfessionalVerified`
- `config/DemoDataConfig.java` — `admin01` 시드
- `config/SecurityConfig.java` — `/api/admin/**`, 401/403 JSON
- `controller/AuthController.java` — logout
- `controller/AdminController.java` — 회원 현황·전문직 승인 API (신규)
- `service/AdminService.java`, `dto/admin/AdminMemberResponse.java` (신규)
- `service/AccountService.java` — 공개 가입 ADMIN 거부
- `service/MemberAuthorizationService.java` — 미인증 전문가 차단(기존) + ADMIN은 회원 API 불가
- `repository/AccountRepository.java` — 역할별 목록 조회
- `erd/*`, `backend/README.md`
- 테스트: `AdminServiceTest`, `AccountServiceTest`, `MemberAuthorizationServiceTest`, `AuthControllerTest`

### 프론트엔드

- `hooks/useAuthSession.js` (신규) — 세션·역할 홈·로그아웃
- `pages/admin/AdminVerificationPage.jsx` (신규) — 종합 현황
- `pages/common/PrivacyPage.jsx` (신규)
- `app/router.jsx`, `pages/auth/LoginPage.jsx`, `pages/auth/SignupPage.jsx`
- `pages/landing/LandingPage.jsx`, `components/layout/AppShell.jsx`, `components/common/Logo.jsx`
- `pages/professional/ProfessionalDashboard.jsx`, `MemberManagementPage.jsx`
- `hooks/useDashboardData.js` — `useMembers({ enabled })` hooks 순서 수정
- `services/apiService.js`, `styles/global.css`

## 검증 결과

- `backend: ./gradlew test` 성공
- `frontend: npm run build` 성공
- Vite 번들 크기 경고만 발생했으며 빌드 오류는 없음

## 참고 사항 (H2)

로컬 기본 프로필은 **메모리 H2** (`jdbc:h2:mem:...`, `ddl-auto: create-drop`)입니다.

- 백엔드 재시작 시 공개 가입 계정은 사라집니다.
- 시드(`member01`, `trainer01`, `doctor01`, `admin01`)만 다시 들어갑니다.
- 가입 → 승인 테스트는 **서버를 끄지 말고** 이어서 하거나, 재시작 후 다시 가입해야 합니다.

## 학원/면접 구분

- **직접 구현·설명 권장**: Account 역할 규칙, `professionalVerified`, 공개 가입 memberId 거부, ADMIN 승인, 소유권(`MemberAuthorizationService`)
- **글루(읽기만)**: 로그아웃 버튼, 랜딩 리다이렉트, 약관 문구, 401 JSON 엔트리포인트, 관리자 화면 레이아웃

## 후속

- [x] `/api/ai/**` permitAll 범위 축소 (08-14 로그인 필수)
- [ ] CSRF 재검토 (현재 `csrf.disable()`, 로컬 시연용)
- [ ] 로컬 H2를 파일 DB로 두면 재시작 후에도 가입분이 남음 (선택)
- [ ] PR #4 `dev` → `main`은 배포 전 인증/권한 확인 후 머지
