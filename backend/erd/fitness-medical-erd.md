# Fitness Medical ERD

```mermaid
erDiagram
    MEMBERS ||--o{ HEALTH_RECORDS : "회원은 건강 기록을 작성한다"
    MEMBERS ||--o{ FEEDBACKS : "회원은 전문가 피드백을 받는다"

    MEMBERS {
        BIGINT id PK "회원 ID"
        VARCHAR name "이름"
        VARCHAR gender "성별"
        INT age "나이"
        DOUBLE height "키"
        DOUBLE weight "체중"
        VARCHAR goal "건강 목표"
        INT progress "목표 진행률"
        VARCHAR status "종합 상태"
        DATE last_measured_date "최근 측정일"
    }

    HEALTH_RECORDS {
        BIGINT id PK "건강 기록 ID"
        BIGINT member_id FK "회원 ID"
        DATE measured_date "측정일"
        INT systolic "수축기 혈압"
        INT diastolic "이완기 혈압"
        INT blood_sugar "혈당"
        DOUBLE weight "체중"
        DOUBLE body_fat "체지방률"
        DOUBLE sleep_hours "수면 시간"
        INT steps "걸음 수"
    }

    FEEDBACKS {
        BIGINT id PK "피드백 ID"
        BIGINT member_id FK "회원 ID"
        VARCHAR author "작성자"
        VARCHAR role "전문가 역할"
        DATE written_date "작성일"
        VARCHAR content "피드백 내용"
    }
```

## 관계 해석

### MEMBERS → HEALTH_RECORDS

`1:N` 관계입니다. 한 회원에게 건강 기록이 여러 개 존재할 수 있지만, 건강 기록 하나는 한 명의 회원에게만 속합니다.

### MEMBERS → FEEDBACKS

`1:N` 관계입니다. 한 회원은 여러 피드백을 받을 수 있지만, 피드백 하나는 한 명의 회원에게만 연결됩니다.

## 향후 추가할 테이블

다음 기능을 직접 구현할 때 ERD에 추가합니다.

- `users`: 로그인 계정
- `professionals`: 의료 전문가와 트레이너 정보
- `goals`: 회원별 건강 목표
- `appointments`: 회원과 전문가 예약
- `professional_members`: 전문가와 담당 회원의 연결

MongoDB의 RAG 문서는 관계형 테이블이 아니므로 이 ERD와 분리해서 관리합니다.
