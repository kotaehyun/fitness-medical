# Fitness Medical ERD

백엔드 데이터베이스 구조를 정리하는 폴더입니다.

## 파일

- [`fitness-medical-erd.md`](./fitness-medical-erd.md): Mermaid로 작성한 현재 ERD
- [`schema.sql`](./schema.sql): 테이블 관계를 공부하기 위한 참고 SQL

## 현재 관계

```text
Member 1 ─── 0..1 Account   (accounts.member_id, UNIQUE)
Member 1 ─── N HealthRecord
Member 1 ─── N Feedback
```

- MEMBER 계정만 회원과 1:1로 연결할 수 있습니다. PROFESSIONAL·ADMIN은 `member_id`가 null입니다.
- 회원 한 명은 여러 건강 기록을 작성할 수 있습니다.
- 회원 한 명은 여러 전문가 피드백을 받을 수 있습니다.
- `accounts.member_id`, `health_records.member_id`, `feedbacks.member_id`는 `members.id`를 참조하는 외래키입니다.

## JPA 코드와 연결

```java
// Account → Member (1:1, optional)
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "member_id", unique = true)
private Member member;

// HealthRecord / Feedback → Member (N:1)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "member_id")
private Member member;
```

계정에 연결된 회원은 삭제 전 `existsByMember_Id`로 막아 FK 위반(500)을 방지합니다.

Entity를 수정하면 이 폴더의 ERD와 `schema.sql`도 함께 갱신해야 합니다.
