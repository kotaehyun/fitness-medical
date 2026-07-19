# Backend 직접 구현 과제

이 문서는 Fitness Medical을 직접 코딩하면서 완성하기 위한 순서입니다. 한 번에 전체 답안을 생성하지 않고, 각 단계에서 먼저 직접 구현한 뒤 오류나 설계 이유를 질문하는 방식을 권장합니다.

## 현재 제공된 기준 코드

- Entity와 Repository 기본 구조
- 회원 전체·상세 조회
- 회원별 건강 기록 조회·등록
- 회원별 피드백 조회
- Validation과 공통 예외 처리
- H2 개발 DB와 시연 데이터

먼저 아래 흐름을 IntelliJ 디버거로 따라가 봅니다.

```text
MemberController
  → MemberService
    → MemberRepository
      → H2 Database
```

## 1단계: 회원 등록

직접 만들 파일과 메서드:

- `dto/member/MemberCreateRequest.java`
- `MemberService.create()`
- `MemberController.createMember()`

요구사항:

- 이름은 필수
- 나이는 1~120
- 목표 진행률은 0~100
- 등록 성공 시 HTTP 201 반환

## 2단계: 회원 수정과 삭제

- 회원 목표와 진행률 수정
- 존재하지 않는 회원이면 404 반환
- 회원 삭제 API 작성
- Controller에는 비즈니스 로직을 작성하지 않기

## 3단계: 전문가 피드백 등록

- `FeedbackRequest` DTO 직접 작성
- 작성자, 역할, 내용 검증
- 피드백 내용은 10자 이상
- 등록일은 서버에서 생성

의료 진단이나 처방처럼 단정적인 문구를 자동 생성하는 기능은 만들지 않습니다.

## 4단계: 로그인과 권한

- 회원과 전문가 계정 Entity 설계
- 비밀번호 암호화
- Spring Security 인증 흐름 학습
- MEMBER와 PROFESSIONAL 권한 분리

처음에는 Session 방식으로 구현한 뒤 필요할 때 JWT로 확장합니다.

## 5단계: MySQL 연결

- Docker MySQL 8.4 데이터베이스 연결
- `mysql` 프로필 환경변수 설정
- H2와 MySQL 계열 DB의 차이 확인
- 초기에는 `ddl-auto=update`로 학습하고, 이후 Flyway 적용 검토

## 6단계: 테스트

- Repository 테스트
- Service 단위 테스트
- Controller `MockMvc` 테스트
- 정상 요청뿐 아니라 Validation 실패와 404도 테스트

## AI 사용 원칙

추천 질문:

- “내가 작성한 Service 코드에서 트랜잭션 범위가 적절한지 리뷰해줘.”
- “이 컴파일 오류가 발생하는 원인만 설명해줘. 완성 코드는 주지 마.”
- “Controller 테스트에 필요한 개념과 작성 순서만 알려줘.”

피해야 할 요청:

- “CRUD 전체를 완성해줘.”
- “로그인 기능을 전부 만들어줘.”
- “에러가 나는데 프로젝트 전체를 알아서 고쳐줘.”

직접 작성한 코드에 대한 리뷰와 디버깅 중심으로 AI를 사용하면 포트폴리오 설명과 면접 준비에도 도움이 됩니다.
