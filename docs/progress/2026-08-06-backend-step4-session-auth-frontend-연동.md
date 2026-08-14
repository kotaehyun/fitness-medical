# 2026-08-06 — STUDY_TASKS 4단계: Session 인증 + 프론트 API 연동

## 결과

4단계(로그인과 권한) **Session 기반 구현 완료**. MEMBER / PROFESSIONAL 분리, BCrypt, 회원가입·로그인·`GET /api/auth/me`. 같은 날 React를 `http://localhost:8081/api`에 연결하고 역할별 화면까지 맞춤.

Entity만 보던 리뷰: [`../study/codeReview/step4-session-auth-review.md`](../study/codeReview/step4-session-auth-review.md) (당시 Controller/Security 이전).  
풀스택 보고: [`../report/2026-08-06-frontend-spring-api-연동.md`](../report/2026-08-06-frontend-spring-api-연동.md).

## 작업한 파일

**백엔드**
- `Account.java`, `AccountRole.java`, `AccountRepository.java`
- `AccountService.java`, `AccountUserDetailsService.java`
- `AuthController.java` — `POST /signup`, `POST /login`, `GET /me`
- `SecurityConfig.java` — BCrypt, signup/login permitAll, 피드백 POST `hasRole("PROFESSIONAL")`
- `WebConfig.java` — CORS `5173`/`5174`, `allowCredentials(true)`

**프론트**
- `.env.local` — `VITE_API_URL=http://localhost:8081/api`, `VITE_USE_MOCK=false`
- `apiService.js` — `credentials: 'include'`, login/me, member/record/feedback 매핑
- `LoginPage.jsx`, `router.jsx` ProtectedRoute
- 회원 대시보드·전문가 상세·피드백·회원 수정/삭제 API 연결

## 설계 결정

- JWT 말고 **Session (`JSESSIONID`)**. 학원 4단계 범위.
- 데모 버튼(`sessionStorage` `fitness-demo-role`)과 실로그인 분리. 데모는 `/auth/me` 안 부름.
- 피드백 POST 권한은 Security `hasRole("PROFESSIONAL")`. 작성자 위조 방지는 08-13에 세션에서 채우도록 바꿈.
- 비밀번호·면허는 로그/응답에 넣지 않음.

## 진행 중 겪은 문제와 해결

1. **8080 → Oracle XML DB 401**  
   Spring이 아니라 Oracle XDB가 8080을 먹고 있었음. `Server: Oracle XML DB` 헤더.  
   → `SERVER_PORT=8081`. 프론트 `VITE_API_URL`도 8081.  
   → [`../troubleshooting/2026-08-01-local-port-8080-oracle-충돌.md`](../troubleshooting/2026-08-01-local-port-8080-oracle-충돌.md)

2. **로그인 후 `/auth/me` 401, 세션이 안 붙음**  
   `fetch`에 `credentials`가 없으면 브라우저가 `Set-Cookie`/`Cookie`를 안 보냄.  
   → 모든 API `credentials: 'include'`. CORS도 `allowCredentials(true)`.

3. **CORS: `5174`만 허용, `5173`에서 막힘**  
   Vite가 5173을 쓰면 preflight 실패.  
   → `localhost:5173`, `5174` 둘 다 허용 + Security `.cors(Customizer.withDefaults())`.

4. **DELETE 204에서 `response.json()` SyntaxError**  
   본문 없는 204를 JSON 파싱하면 터짐.  
   → `apiService.request()`에서 `status === 204`이면 `null` 반환.

5. **검증 실패 메시지가 기본 영어/애매함 (08-05)**  
   → `dc7b400` 어노테이션 커스텀 `message` 사용.

6. **ProtectedRoute가 로그인 직후 로그인 화면으로 튕김 (08-13에 재발, 원인 동일 계열)**  
   `/auth/me` 401이 React Query에 캐시된 채로 로그인 성공해도 예전 데이터.  
   → 08-13에 로그인 성공 시 `setQueryData(['auth','me'], account)`.

## 실행·검증

```bash
cd backend && SERVER_PORT=8081 ./gradlew bootRun
cd frontend && npm run dev   # VITE_API_URL=8081, VITE_USE_MOCK=false
```

- `./gradlew compileJava` 성공, `npm run build` 성공 (청크 크기 경고만)
- 시드: `member01` / `trainer01` / `doctor01`, 비밀번호 `password123`

## 커밋

- `1a5b001` Session 인증
- `e52b344` 피드백 POST PROFESSIONAL
- `d7f4ad6` 프론트 Spring 연동
- `13d66d8` 피드백 페이지
- `4787ff8` 회원 CRUD 연결
- PR #1 `42a53d1` → `dev`

## 다음에 할 일 (당시 → 이후)

- [x] Account–Member 1:1 (08-08)
- [x] 회원 API 소유권·피드백 author 세션 (08-13)
- [ ] JWT는 안 함 (Session 유지)
