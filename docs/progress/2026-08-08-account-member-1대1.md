# 2026-08-08 — Account와 Member 1:1 연결

## 결과

로그인 계정(`Account`)과 회원 프로필(`Member`)을 **1:1**로 연결. MEMBER만 `accounts.member_id` FK, PROFESSIONAL은 null. 회원가입 규칙을 Service에서 검증하고 테스트로 고정.

## 작업한 파일

- `Account.java` — `@OneToOne` + `@JoinColumn(name = "member_id", unique = true)`, LAZY
- `AccountService.resolveMember()` — 역할별 Member 연결
- `AccountRepository.existsByMember_Id(Long)` — 이미 계정 있는 회원 중복 방지
- `erd/schema.sql`, `erd/fitness-medical-erd.md`
- `AccountServiceTest` — MEMBER 프로필 필수, PROFESSIONAL member 없음 등

## 설계 결정

- owning side는 **Account**. Member에 Account 역참조 필드 없음 (FK는 accounts에만).
- `member_id` UNIQUE → 회원 1명당 계정 최대 1개.
- 당시에는 MEMBER가 **기존 memberId 연결**도 가능했음 (관리자/초대 가정).  
  → **08-13에 공개 가입 memberId는 전부 400.** 공개 API로 다른 회원 가로채기 가능했기 때문 (`a90684c`).

## 진행 중 겪은 문제와 해결

1. **PROFESSIONAL에 member를 붙이면 안 되는데 DTO에 memberId가 열려 있었음**  
   역할만 보고 저장하면 전문가 계정이 회원 FK를 가짐.  
   → Service에서 PROFESSIONAL + memberId 조합 거절. 08-13엔 공개 가입 어떤 역할이든 memberId면 400.

2. **같은 Member에 계정 두 개**  
   UNIQUE 없이 저장하면 1:1이 깨짐.  
   → DB `uk_accounts_member_id` + `existsByMember_Id`로 가입 전 검사.

3. **LAZY `account.getMember()`를 트랜잭션 밖에서 접근**  
   `/auth/me` DTO 변환 시 LazyInitializationException 가능.  
   → `AccountResponse.from()`은 서비스 트랜잭션 안에서 호출. member null이면 memberId null.

4. **면접/공부 포인트**  
   Entity를 JSON으로 그대로 내리면 password·LAZY 프록시가 나감. 반드시 DTO.  
   password·licenseNumber는 `AccountResponse`에 없음.

## 커밋

- `6315c20` feat: Account와 Member를 1:1로 연결하고 회원가입 규칙을 검증한다
- `f145e0a` Merge `feat/account-member-link` → `dev`

## 다음에 할 일 (당시 → 이후)

- [x] RAG `/ask` (08-11, 08-12, 08-13)
- [x] 공개 가입 memberId 거부 + 미인증 전문가 (08-13)
