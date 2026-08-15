# Fitness Medical ERD

```mermaid
erDiagram
    MEMBERS ||--o| ACCOUNTS : "회원은 계정 0~1개"
    MEMBERS ||--o{ HEALTH_RECORDS : "회원은 건강 기록을 작성한다"
    MEMBERS ||--o{ FEEDBACKS : "회원은 전문가 피드백을 받는다"
    MEMBERS ||--o| MEMBER_ASSIGNMENTS : "회원은 담당 연결 0~1개"
    ACCOUNTS ||--o{ MEMBER_ASSIGNMENTS : "전문의 담당"
    ACCOUNTS ||--o{ MEMBER_ASSIGNMENTS : "트레이너 담당"
    ACCOUNTS ||--o{ CHAT_MESSAGES : "메시지를 보낸다"
    ACCOUNTS ||--o{ CHAT_MESSAGES : "메시지를 받는다"

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

    ACCOUNTS {
        BIGINT id PK "계정 ID"
        VARCHAR login_id UK "로그인 아이디"
        VARCHAR password "암호화된 비밀번호"
        VARCHAR display_name "표시 이름"
        VARCHAR role "MEMBER, PROFESSIONAL 또는 ADMIN"
        VARCHAR professional_type "TRAINER 또는 PHYSICIAN"
        VARCHAR license_number UK "전문의 면허 또는 트레이너 자격번호(NULL 허용)"
        BOOLEAN professional_verified "전문직 인증 여부"
        BIGINT member_id FK_UK "연결 회원(없으면 null)"
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

    MEMBER_ASSIGNMENTS {
        BIGINT id PK "담당 ID"
        BIGINT member_id FK_UK "회원"
        BIGINT physician_account_id FK "담당 전문의 계정"
        BIGINT trainer_account_id FK "담당 트레이너 계정"
    }

    CHAT_MESSAGES {
        BIGINT id PK "메시지 ID"
        BIGINT sender_account_id FK "보낸 계정"
        BIGINT receiver_account_id FK "받는 계정"
        VARCHAR body "텍스트"
        TIMESTAMP created_at "작성 시각"
    }
```

## 관계 해석

### MEMBERS → ACCOUNTS

`1:0..1` 관계입니다. MEMBER 역할 계정만 `member_id`로 회원과 연결되고, PROFESSIONAL·ADMIN은 `member_id`가 null입니다. `member_id`는 UNIQUE라서 한 회원당 계정은 최대 1개입니다. ADMIN은 공개 가입 불가(시드)이며 전문직 `professional_verified`만 승인/해제합니다.

### MEMBERS → HEALTH_RECORDS

`1:N` 관계입니다. 한 회원에게 건강 기록이 여러 개 존재할 수 있지만, 건강 기록 하나는 한 명의 회원에게만 속합니다.

### MEMBERS → FEEDBACKS

`1:N` 관계입니다. 한 회원은 여러 피드백을 받을 수 있지만, 피드백 하나는 한 명의 회원에게만 연결됩니다.

## 향후 추가할 테이블

다음 기능을 직접 구현할 때 ERD에 추가합니다.

- `professionals`: 의료 전문가와 트레이너 정보
- `goals`: 회원별 건강 목표
- `appointments`: 회원과 전문가 예약

MongoDB의 RAG 문서는 관계형 테이블이 아니므로 이 ERD와 분리해서 관리합니다.
