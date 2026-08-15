# 2026-08-14 — Repository 테스트 + 파일 H2

## 결과

STUDY_TASKS 6단계 Repository 테스트: `@DataJpaTest`로 `AccountRepository` 3개를 실제 H2 SQL로 검증.

로컬 `local` 프로필은 메모리 H2(`create-drop`) 대신 **파일 H2 + `ddl-auto: update`**. 서버를 다시 켜도 공개 가입이 안 지워진다. 시드는 `member01`이 있으면 건너뛴다.

## 작업한 파일

- `AccountRepositoryTest.java` — `findByLoginId` / `existsByLoginId`(없는 아이디) / `existsByLicenseNumber`
- `DemoDataConfig.java` — `existsByLoginId("member01")`이면 `return`
- `application-local.yml` — `jdbc:h2:file:./data/fitnessmedical`, `ddl-auto: update`
- `.gitignore` — `backend/data/`

## 설계 결정

- Repository는 비밀번호를 **해시하지 않는다**. 받은 문자열을 그대로 저장. 해시는 Service(`PasswordEncoder`).
- `ProfessionalType`은 `TRAINER` / `PHYSICIAN`만. 한 계정에 유형 하나.
- `@DataJpaTest`는 이 yml을 안 탄다. 테스트는 임베디드 메모리 H2.
- Flyway·CSRF·`mysql` 프로필은 이번에 안 함.

## 진행 중 겪은 문제와 해결

1. **`save()` / `save();` 컴파일 실패**  
   세미콜론만 있으면 `found: no arguments`. `save(S entity)`라서 `save(account)`가 필요.

2. **비밀번호 `isNotEqualTo` 실패**  
   Service BCrypt 기대를 Repo 테스트에 넣음. Repo는 평문 그대로 → `isEqualTo`.

3. **`isBlank(displayName)` 컴파일 실패**  
   `isBlank()`는 인자 없음(빈 문자열 여부). 이름 비교는 `isEqualTo`.

4. **빈 테스트도 BUILD SUCCESSFUL**  
   assertion이 없으면 JUnit은 통과. `existsByLoginId_unknown_isFalse` region을 채운 뒤에야 의미가 있음.

5. **`ProfessionalType.PHYSICAL_THERAPIST`**  
   enum에 없음 (`cannot find symbol`). `PHYSICIAN` 또는 `TRAINER`.

6. **파일 H2만 바꾸면 두 번째 기동 UNIQUE**  
   `CommandLineRunner`가 매 기동 `saveAll`. `member01` 가드를 먼저 넣음.

## 검증

```bash
./gradlew test --tests AccountRepositoryTest
./gradlew test
```

성공. 기동 로그: `jdbc:h2:file:./data/fitnessmedical`. 파일: `backend/data/fitnessmedical.mv.db` (gitignore).

## 다음에 할 일

- [ ] Flyway (STUDY_TASKS 5, Docker MySQL)
- [ ] CSRF 재검토 (`csrf.disable()`, 로컬 시연)
- [ ] PR #4 `dev`→`main` (아직 머지하지 않음)
