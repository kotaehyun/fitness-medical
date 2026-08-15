# 세션 인증 · 권한 · RAG 입구 (구현 후)

[`step4-session-auth-review.md`](./step4-session-auth-review.md)는 Entity/Repository만 본 **구현 전** 노트다. 오타·미구현 가정도 그대로 둔다. 이 문서는 현재 코드 기준이다. 역할·가입 규칙은 [`../../decisions/계정-역할-소유권.md`](../../decisions/계정-역할-소유권.md).

진단·처방 API가 아니다. 비밀번호·면허번호는 로그·응답에 넣지 않는다.

## 로그인 (세션)

```text
POST /api/auth/login
  → AuthController
  → AuthenticationManager.authenticate(UsernamePasswordAuthenticationToken)
  → AccountUserDetailsService.loadUserByUsername(loginId)
  → AccountRepository.findByLoginId
  → DaoAuthenticationProvider: BCryptPasswordEncoder.matches(평문, DB 해시)
  → SecurityContextHolder에 Authentication
  → HttpSessionSecurityContextRepository.saveContext → HTTP 세션 + 쿠키
```

이후 요청은 쿠키로 세션을 찾고 FilterChain이 SecurityContext를 복원한다. JWT가 아니다. 프론트는 `credentials: 'include'`.

`UserDetails`에는 username(loginId)·해시·역할만 있다. `memberId`·`professionalVerified`는 이후 `MemberAuthorizationService`가 Account를 다시 읽는다.

## FilterChain 순서 (위에서 첫 매칭)

`SecurityConfig` — CSRF는 로컬 시연용 disable.

| 매칭 | 규칙 |
|------|------|
| signup / login / logout / H2 | permitAll |
| `/api/auth/me` | authenticated |
| `POST /api/members/*/feedback` | ROLE_PROFESSIONAL |
| `/api/admin/**` | ROLE_ADMIN |
| `/api/members/**` | authenticated (소유권은 Service) |
| `/api/ai/**` | authenticated (`/api/**` permitAll보다 앞) |
| 나머지 `/api/**` | permitAll (로컬 학습) |

미인증 401, 권한 없음 403. JSON 본문.

## 회원 데이터 소유권

`MemberAuthorizationService`

- MEMBER: 연결된 `member.id`만
- PROFESSIONAL: `professionalVerified == true`일 때만 전체 회원
- ADMIN: 이 서비스로 건강 API를 열지 않음. `/api/admin/**`만

공개 가입 `memberId`는 `AccountService.resolveMember`가 400. MEMBER는 프로필로 새 Member. PROFESSIONAL은 `member == null`.

## RAG

브라우저 → Spring `POST /api/ai/ask` (로그인 필수) → FastAPI `:8000` `/ask`  
Router → Retriever(Chroma, `MAX_DISTANCE=0.55`) → Writer → Guard.

FastAPI 자체 인증은 없다. 로컬은 127.0.0.1. 생활 안내. 임상 답이 아니다.

## 로컬 vs 이후

파일 H2 + `ddl-auto: update`는 학습용. CSRF·Flyway·FastAPI 인증은 운영 전에 다시 본다.
