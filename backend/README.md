# Fitness Medical Backend

Fitness Medical의 Spring Boot REST API입니다. 학원 백엔드 과정에서 주로 사용하는 계층형 구조를 적용했습니다.

## 기술 스택

- Java 21
- Spring Boot 3.5
- Spring Web, Validation
- Spring Data JPA
- Spring Security
- MySQL 8.4
- H2(local 프로필)

## 실행 준비

Java 21과 Gradle Wrapper가 준비되어 있습니다.

```bash
cd backend
./gradlew bootRun
```

기본 `local` 프로필은 메모리 H2 DB를 사용하고 애플리케이션 시작 시 시연 데이터를 생성합니다.

## API

| Method | URL | 설명 |
|---|---|---|
| GET | `/api/members` | 전체 회원 조회 |
| GET | `/api/members/{id}` | 회원 상세 조회 |
| GET | `/api/members/{id}/records` | 회원 건강 기록 조회 |
| POST | `/api/members/{id}/records` | 건강 기록 등록 |
| GET | `/api/members/{id}/feedback` | 회원 피드백 조회 |

## 패키지 구조

```text
com.fitnessmedical/
├── controller/  # HTTP 요청과 응답
├── service/     # 비즈니스 로직
├── repository/  # JPA 데이터 접근
├── entity/      # 데이터베이스 테이블 객체
├── dto/         # 요청·응답 데이터 객체
├── config/      # CORS, Security, 시연 데이터
└── common/      # 공통 예외와 오류 응답
```

## IntelliJ에서 열기

1. IntelliJ에서 `Open`을 선택합니다.
2. 전체 저장소가 아니라 `Fitness_medical/backend` 폴더를 선택합니다.
3. Gradle 프로젝트 로딩이 끝날 때까지 기다립니다.
4. Project SDK와 Gradle JVM이 `Java 21`인지 확인합니다.
5. `FitnessMedicalApplication`의 실행 버튼을 누릅니다.

Gradle 설정은 `Wrapper`를 사용해야 팀이나 다른 컴퓨터에서도 동일한 버전으로 실행됩니다.

## 학습 방식

현재 회원 조회와 건강 기록 등록 코드는 계층별 흐름을 확인하기 위한 참고 코드입니다. 이후 기능은 AI가 완성 코드를 대신 작성하지 않고 [`STUDY_TASKS.md`](./STUDY_TASKS.md)의 명세를 보고 직접 구현하는 방식으로 진행합니다.

Java 문법과 Spring 코드를 읽는 순서는 [`JAVA_CODE_GUIDE.md`](./JAVA_CODE_GUIDE.md)를 확인하세요. 핵심 소스에도 어노테이션과 실행 흐름을 설명하는 한국어 주석이 포함되어 있습니다.

데이터베이스 관계는 [`erd/fitness-medical-erd.md`](./erd/fitness-medical-erd.md)에서 확인할 수 있습니다.

## 환경별 YAML 설정

Spring Boot는 기본 설정 파일과 현재 활성화된 프로필 설정 파일을 합쳐서 사용합니다.

| 파일 | 용도 |
|---|---|
| `application.yml` | 서버 포트, JSON, JPA, 로깅 등 모든 환경의 공통 설정 |
| `application-local.yml` | 기본 학습 환경인 메모리 H2 설정 |
| `application-mysql.yml` | Docker MySQL 8.4 연결과 커넥션 풀 설정 |

아무 옵션 없이 실행하면 `local`이 기본 적용됩니다.

```bash
./gradlew bootRun
```

IntelliJ에서 MySQL 프로필을 사용할 때는 Run Configuration의 Environment variables에
`SPRING_PROFILES_ACTIVE=mysql`을 입력합니다.

## MySQL 실행

기존 MariaDB는 3306 포트로 유지합니다. Fitness Medical Docker MySQL 호스트 포트는 환경마다 다를 수 있습니다.
**RTX 4090 노트북** 기본값은 `3308`입니다(이 머신에서 `3307`은 로컬 mysqld가 사용). Mac 등에서는 `3307` 등 다른 포트를 쓸 수 있습니다.
자세한 내용은 [`../docker/README.md`](../docker/README.md)를 참고하세요.

```bash
SPRING_PROFILES_ACTIVE=mysql ./gradlew bootRun
```

환경변수로 연결 정보를 변경할 수도 있습니다. (예시는 RTX 4090 노트북의 `3308` 기준)

```bash
SPRING_PROFILES_ACTIVE=mysql \
DB_URL='jdbc:mysql://localhost:3308/fitness_medical?serverTimezone=Asia/Seoul&characterEncoding=UTF-8' \
DB_USERNAME=fitness_user \
DB_PASSWORD=fitness_dev_2026 \
./gradlew bootRun
```

## 프론트엔드 연동

프론트 개발 서버 `http://localhost:5173`과 `http://localhost:5174`에서 `/api/**`를 호출할 수 있도록 CORS가 설정되어 있습니다. 세션 로그인은 `JSESSIONID` 쿠키를 사용하므로 프론트 요청에 credentials 포함이 필요합니다. CORS 또는 서버 포트를 변경한 뒤에는 백엔드를 재시작해야 합니다.
