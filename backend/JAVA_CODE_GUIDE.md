# Java와 Spring 코드 읽는 순서

Java를 다시 공부하면서 Fitness Medical 백엔드를 이해하기 위한 순서입니다.

## 1. Java 기본 문법 복습

먼저 다음 파일을 엽니다.

```text
entity/MemberStatus.java
entity/Member.java
dto/member/MemberResponse.java
```

확인할 내용:

- `class`와 `enum`의 차이
- 필드와 생성자
- `public`, `protected`, `private`
- Getter
- `static` 메서드
- Java `record`
- `List<Member>` 같은 Generic

## 2. Entity 확인

`entity/Member.java`를 읽습니다.

```java
@Entity
public class Member {
    @Id
    private Long id;
}
```

- `@Entity`: JPA가 관리하는 객체
- `@Id`: 테이블 기본키
- `Long`: null을 표현할 수 있는 참조 타입
- `long`: null을 표현할 수 없는 기본 타입

처음 생성한 회원은 DB 저장 전까지 id가 없으므로 `Long`이 적합합니다.

## 3. Repository 확인

`repository/MemberRepository.java`를 읽습니다.

```java
public interface MemberRepository extends JpaRepository<Member, Long> {
}
```

직접 구현 클래스가 없는데도 사용할 수 있는 이유는 Spring Data JPA가 실행 중 구현체를 만들어 주기 때문입니다.

## 4. Service 확인

`service/MemberService.java`를 읽습니다.

Service에서 확인할 내용:

- 생성자 주입
- `final`
- `Optional`과 `orElseThrow`
- Entity를 DTO로 변환하는 이유
- `@Transactional(readOnly = true)`

IntelliJ에서 `findById()` 왼쪽에 breakpoint를 걸고 요청을 보내면 실행 흐름을 확인할 수 있습니다.

## 5. Controller 확인

`controller/MemberController.java`를 읽습니다.

```java
@GetMapping("/{memberId}")
public MemberResponse getMember(@PathVariable Long memberId) {
    return memberService.findById(memberId);
}
```

`GET /api/members/1` 요청에서 URL의 `1`이 `memberId`에 들어갑니다. Controller는 Service를 호출하고 결과를 JSON으로 반환합니다.

## 6. 직접 따라 쓰기

복사와 붙여넣기를 하지 말고 다음 순서로 연습합니다.

1. 기존 코드를 주석 처리하거나 별도 연습 파일을 만듭니다.
2. Controller 메서드 하나를 직접 다시 작성합니다.
3. 컴파일 오류를 읽습니다.
4. 오류 원인을 먼저 추측합니다.
5. 해결되지 않을 때 오류 메시지와 작성한 코드만 질문합니다.

## 7. IntelliJ에서 유용한 기능

- `Command + 클릭`: 클래스나 메서드 선언으로 이동
- `Option + F7`: 해당 코드가 사용되는 위치 찾기
- `Control + R`: 현재 실행 설정 실행
- 줄 번호 왼쪽 클릭: breakpoint 설정
- `Shift` 두 번: 파일이나 클래스 검색

## 첫 번째 직접 코딩 과제

[`STUDY_TASKS.md`](./STUDY_TASKS.md)의 회원 등록 기능부터 시작합니다. 먼저 `MemberCreateRequest` DTO만 직접 작성하고 컴파일해 보는 것을 권장합니다.
