# 2026-08-04 — Backend STUDY_TASKS 3단계: 전문가 피드백 등록

## 결과

STUDY_TASKS.md 3단계(전문가 피드백 등록) **완료**. DTO 검증 → Service 로직 → Controller API → 실제 저장까지 curl로 end-to-end 확인함. 같은 날 2단계(회원 수정·삭제)의 실행 테스트도 함께 확인 완료.

## 작업한 파일

- `backend/src/main/java/com/fitnessmedical/dto/feedback/FeedbackRequest.java` (신규)
- `backend/src/main/java/com/fitnessmedical/service/FeedbackService.java` (`create()` 메서드 추가)
- `backend/src/main/java/com/fitnessmedical/controller/MemberController.java` (`createFeedback()` 메서드 추가, `POST /api/members/{memberId}/feedback`)

## 설계 결정

- `FeedbackRequest`엔 `author`, `role`, `content` 3개만 포함. `member`는 URL의 `{memberId}`로 별도 전달(`HealthRecordRequest`와 동일 패턴), `writtenDate`는 "등록일은 서버에서 생성" 요구사항에 따라 `FeedbackService.create()`에서 `LocalDate.now()`로 채움.
- `content`는 새로운 검증 어노테이션 `@Size(min = 10)` 사용 (문자열 길이 검사, `@Min`/`@Max`는 숫자 전용이라 여기선 안 맞음).

## 실행 테스트 결과 (2026-08-04)

**3단계 (피드백)**
- 정상 등록 → `writtenDate`가 서버에서 오늘 날짜(`2026-08-04`)로 자동 생성됨 확인
- `content` 10자 미만 → `400`, `"content 입력값을 확인해 주세요."`
- 존재하지 않는 회원 → `404`, `"회원을 찾을 수 없습니다."`

**2단계 (회원 수정·삭제, 지난 세션에 코드만 완료하고 테스트 못 했던 부분)**
- `PUT /api/members/2` → `200`, `goal`/`progress`만 바뀌고 나머지 필드 그대로 (더티 체킹 정상 동작 확인)
- `PUT /api/members/9999` → `404`
- `DELETE /api/members/2` → `204`, 본문 없음
- 삭제 후 `GET /api/members/2` → `404` (실제 삭제 확인)

## 브랜치 전략 (새로 정한 방식)

이번부터 **단계별로 브랜치를 분리**하기로 함. 3단계는 `feat/feedback-api` 브랜치로 새로 만들었는데, `dev`가 아니라 **`feat/member-update-api`(2단계 브랜치) 위에서 분기**했음 — 3단계 코드(`MemberController.createFeedback()`)가 실제로는 2단계까지 완료된 `MemberController.java` 파일 위에 이어서 작성된 상태였기 때문에, 2단계를 밑에 깔고 쌓는(stacked) 구조가 자연스러웠음.

```text
dev
 └─ feat/member-update-api (2단계, 커밋 35657b3, origin에 push 대기 중)
     └─ feat/feedback-api (3단계, 커밋 9a8e6cc, 신규 브랜치, 원격엔 아직 없음)
```

머지할 때는 `feat/member-update-api → dev` 먼저 머지하고, 그다음 `feat/feedback-api → dev`(또는 `feat/member-update-api`에 머지된 뒤 `dev`로) 순서로 진행하는 게 자연스러움. 두 브랜치를 각각 독립적으로 `dev`에 머지하려고 하면 `feat/feedback-api`엔 2단계 커밋이 이미 포함되어 있어서 충돌 없이 잘 들어갈 것으로 예상됨 (검증은 실제 머지 시점에 필요).

## 커밋/브랜치 상태 (2026-08-04)

- `feat/member-update-api`: `35657b3` — 여전히 origin보다 1커밋 앞서있음 (push 필요)
- `feat/feedback-api`: `9a8e6cc` (신규 브랜치, 로컬에만 존재, origin에 없음 — push 필요)
- push는 이 세션 환경 네트워크 제약으로 못 함 — 사용자가 직접 진행

## 다음에 할 일

- [ ] `feat/member-update-api`, `feat/feedback-api` 둘 다 push
- [ ] `dev`로 머지 (순서: member-update-api 먼저 → feedback-api)
- [ ] STUDY_TASKS 4단계(로그인과 권한)로 이어서 진행 예정
