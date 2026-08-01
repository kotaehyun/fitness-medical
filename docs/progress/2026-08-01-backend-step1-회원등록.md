# 2026-08-01 — Backend STUDY_TASKS 1단계: 회원 등록

## 결과

STUDY_TASKS.md 1단계(회원 등록) **완료**. DTO 검증 → Service 로직 → Controller API → 실제 저장까지 curl로 end-to-end 확인함.

## 작업한 파일

- `backend/src/main/java/com/fitnessmedical/dto/member/MemberCreateRequest.java` (신규 작성)
- `backend/src/main/java/com/fitnessmedical/service/MemberService.java` (`create()` 메서드 추가)
- `backend/src/main/java/com/fitnessmedical/controller/MemberController.java` (`createMember()` 메서드 추가, `POST /api/members`)

## 진행 중 겪은 문제와 해결

1. **record 문법 실수** — record 컴포넌트 목록에서 콤마 누락, trailing comma로 컴파일 에러. record 괄호 안은 "필드 선언"이 아니라 "생성자 파라미터 목록"이라는 점 확인.
2. **어노테이션 타입 불일치** — `int`/`double` 필드에 `@NotBlank`(String 전용) 오적용. 규칙 정리: `String`→`@NotBlank`, `int`→`@Min`/`@Max`, `double`→`@DecimalMin`/`@DecimalMax`.
3. **`@Entity` 오적용** — DTO record에 JPA `@Entity`가 붙어있던 것 제거.
4. **불필요/존재하지 않는 필드** — `quantity`(요구사항에 없음), `joinDate`(Member 엔티티에 실제로 없는 필드) 둘 다 삭제.
5. **파일/클래스 혼동** — `create(MemberCreateRequest)` 메서드를 처음에 `HealthRecordService.java`에 잘못 추가함 (`memberRepository` 심볼 없음 에러의 원인). `MemberService.java`로 이동해서 해결.
6. **로컬 포트 충돌** — `localhost:8080`이 Oracle Database의 XML DB(XDB) HTTP 리스너와 충돌해서 POST 요청이 `401 Unauthorized (WWW-Authenticate: Basic realm="XDB")`로 응답됨. Spring Boot 앱 자체 문제가 아니었음. `SERVER_PORT=8081 ./gradlew bootRun`으로 우회. 자세한 내용은 [`../troubleshooting/2026-08-01-local-port-8080-oracle-충돌.md`](../troubleshooting/2026-08-01-local-port-8080-oracle-충돌.md) 참고.

## 설계 결정

`MemberCreateRequest`에 어떤 필드를 넣을지(클라이언트 입력 vs 서버 결정)는 [`../decisions/member-create-request-필드-설계.md`](../decisions/member-create-request-필드-설계.md)에 별도로 기록함.

## 다음 단계

STUDY_TASKS 2단계 — 회원 수정과 삭제 (목표/진행률 수정, 404 처리, 삭제 API, Controller에 비즈니스 로직 넣지 않기).
