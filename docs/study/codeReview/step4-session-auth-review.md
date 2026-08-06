# STUDY_TASKS 4단계 — Session 인증 코드 리뷰

## 현재 범위

이번 리뷰는 Session 인증의 기반이 되는 다음 세 파일을 다룹니다.

- `AccountRole.java`: 회원과 전문가 권한 구분
- `Account.java`: 로그인 계정 JPA Entity
- `AccountRepository.java`: 로그인 아이디 조회와 중복 확인

아직 로그인 Controller, Service, `UserDetailsService`, Session 설정은 구현하지 않았습니다.

## 전체 구조

```text
AccountRole
    ↓ Account가 역할을 저장
Account Entity
    ↓ AccountRepository가 DB에 접근
accounts 테이블
```

기존 `Member`는 건강관리 대상 데이터이고, `Account`는 인증 정보입니다. 두 책임을 분리하면 로그인 기능 때문에 회원 건강 데이터 구조가 불필요하게 복잡해지는 것을 막을 수 있습니다.

## AccountRole 리뷰

```java
public enum AccountRole {
    MEMBER,
    PROFESSIONAL
}
```

- 문자열을 자유롭게 입력하지 않고 Enum으로 제한해 오타와 잘못된 역할 저장을 방지합니다.
- JPA에서는 `@Enumerated(EnumType.STRING)`으로 저장하여 순서값보다 의미가 분명합니다.
- Spring Security 권한으로 변환할 때는 일반적으로 `ROLE_` 접두사를 붙입니다.

```text
MEMBER → ROLE_MEMBER
PROFESSIONAL → ROLE_PROFESSIONAL
```

### 이번에 발견한 실수

- `PROFESSIONAL`을 `RPOFESSIONAL`로 작성했습니다.
- Enum 상수는 문법적으로 유효하면 컴파일되므로, 컴파일 성공만으로 철자까지 검증되지는 않습니다.

## Account Entity 리뷰

### 테이블과 로그인 아이디

- 테이블명은 `accounts`입니다.
- `login_id`에는 unique constraint를 적용해 DB 수준에서도 중복을 방지합니다.
- 애플리케이션의 `existsByLoginId()` 검사와 DB unique constraint를 함께 사용해야 동시 요청에도 안전합니다.

### 비밀번호

- DB에는 평문 비밀번호를 저장하면 안 됩니다.
- 회원가입 Service에서 `PasswordEncoder.encode(rawPassword)` 결과만 Entity 생성자에 전달해야 합니다.
- 로그인 검증은 문자열 비교가 아니라 `PasswordEncoder.matches(rawPassword, encodedPassword)`를 사용합니다.

### 역할 저장

```java
@Enumerated(EnumType.STRING)
private AccountRole role;
```

`ORDINAL`은 Enum 순서가 바뀌면 기존 데이터 의미가 달라질 수 있으므로 `STRING`이 안전합니다.

### 기본 생성자와 Getter

- JPA가 조회 결과로 객체를 만들기 위해 기본 생성자가 필요합니다.
- 외부에서 의미 없이 생성하지 못하도록 `protected`로 제한했습니다.
- ID 필드는 저장 전 `null`일 수 있으므로 Getter 반환형도 원시 타입 `long`이 아니라 `Long`을 사용합니다.

### 개선 가능한 스타일

- 불필요한 빈 줄을 정리하면 Entity 구조가 더 잘 보입니다.
- `@Column(name="login_id")`는 프로젝트 스타일에 맞춰 `name = "login_id"`로 띄어쓰기를 통일할 수 있습니다.
- 이 항목들은 기능 오류가 아니며 별도 코드 정리 시 처리할 수 있습니다.

## AccountRepository 리뷰

```java
public interface AccountRepository extends JpaRepository<Account, Long>
```

- 첫 번째 제네릭 `Account`: Repository가 관리할 Entity
- 두 번째 제네릭 `Long`: Entity 기본키 타입
- `JpaRepository`: `save`, `findById`, `findAll`, `delete` 등 기본 CRUD 제공

### 쿼리 메서드

```java
Optional<Account> findByLoginId(String loginId);
boolean existsByLoginId(String loginId);
```

- `findByLoginId()`: 로그인할 계정을 조회합니다.
- `Optional<Account>`: 존재하지 않는 아이디를 `null` 대신 명시적으로 처리합니다.
- `existsByLoginId()`: 회원가입 전에 중복 여부를 검사합니다.

### 이번에 발견한 실수

1. 잘못된 import:

```java
org.springframework.jpa.repository.JpaRepository
```

정확한 경로:

```java
org.springframework.data.jpa.repository.JpaRepository
```

2. `extends JpaRepository<Account, Long>` 누락:

단순 Java 인터페이스로는 컴파일될 수 있지만 Spring Data Repository Bean과 기본 CRUD 기능이 생성되지 않습니다.

## 면접용 설명

“건강관리 대상인 Member와 로그인 인증 책임을 가진 Account를 분리했습니다. Account의 역할은 EnumType.STRING으로 저장하고, loginId는 애플리케이션 중복 검사와 DB unique constraint로 보호합니다. AccountRepository는 JpaRepository를 상속하며 Optional 기반 로그인 아이디 조회 메서드를 제공합니다. 비밀번호는 이후 Service에서 BCrypt로 암호화한 값만 저장할 예정입니다.”

## 다음 구현 순서

1. `PasswordEncoder` Bean 등록
2. 회원가입 요청 DTO Validation
3. 회원가입 Service에서 아이디 중복 확인과 BCrypt 암호화
4. 로그인 사용자 조회를 위한 `UserDetailsService`
5. Session 로그인 Controller와 SecurityFilterChain 설정
6. MEMBER / PROFESSIONAL 권한별 API 접근 테스트
