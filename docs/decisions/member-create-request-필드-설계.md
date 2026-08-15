# 결정: MemberCreateRequest에 어떤 필드를 넣을 것인가

## 배경

`Member` 엔티티는 `name, gender, age, height, weight, goal, progress, status, lastMeasuredDate` 9개 필드(+id)를 가진다. 회원 등록 요청 DTO(`MemberCreateRequest`)에 이 중 무엇을 클라이언트 입력으로 받고, 무엇을 서버가 기본값으로 정할지 결정이 필요했다 (STUDY_TASKS.md 1단계에서 "직접 결정"으로 명시된 부분).

## 결정

| 필드 | 처리 | 이유 |
|---|---|---|
| `name`, `gender`, `age`, `height`, `weight`, `goal` | DTO 포함, 클라이언트 입력 | 회원가입 시점에 사용자가 직접 입력하는 게 자연스러운 값 |
| `progress` | DTO 포함, 클라이언트 입력 | STUDY_TASKS 요구사항에 "목표 진행률 0~100" 검증이 명시되어 있어 입력값으로 확정 |
| `status` | DTO 제외, 서버 기본값 | 가입 시점엔 아직 측정 이력이 없어 판단 근거가 없음. `MemberService.create()`에서 `MemberStatus.CHECK_REQUIRED`로 고정 |
| `lastMeasuredDate` | DTO 제외, 서버 기본값(`null`) | "최근 측정일"은 건강 기록이 실제로 쌓여야 의미가 있는 값. 가입 화면에서 입력받는 게 부자연스러움 |

## 제외한 필드

- `quantity` — 초안 작성 중 다른 예제(쇼핑몰류)에서 복사된 것으로 추정되는 필드. 요구사항에 대응하는 게 없어 삭제.
- `joinDate` — `Member` 엔티티에 실제로 존재하지 않는 필드. 저장할 곳이 없어 삭제.

## 재검토 시점

- STUDY_TASKS 2단계(수정)에서 `status`를 언제/어떻게 갱신할지 다시 다뤄야 함 (예: 건강 기록 등록 시 자동 갱신할지, 전문가가 수동으로 바꿀지).
- 로그인/계정(4단계) 이후 공개 가입은 기존 `memberId`를 받지 않고 MEMBER 프로필로 새 Member를 만든다. 전문직 승인·소유권은 [`계정-역할-소유권.md`](./계정-역할-소유권.md).
