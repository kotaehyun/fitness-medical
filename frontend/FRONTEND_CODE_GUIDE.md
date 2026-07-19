# Fitness Medical 프론트엔드 발표·학습 가이드

이 문서는 코드를 외우기 위한 자료가 아니라, 프로젝트의 핵심 설계를 자신의 말로 설명하기 위한 자료입니다.

## 1. 발표할 때 설명할 전체 흐름

```text
URL
  → React Router
  → Page 컴포넌트
  → TanStack Query Hook
  → healthService
  → mockService 또는 apiService
  → 화면 렌더링
```

예를 들어 회원 대시보드에 접속하면 Router가 페이지를 선택합니다. 페이지는 `useMemberData()`를 호출하고, Query Hook은 `healthService.getMember()`를 실행합니다. 개발 기본값에서는 mock 데이터를 반환하고, 환경변수를 바꾸면 Spring Boot REST API를 호출합니다.

## 2. 하드코딩한 부분과 분리한 부분

### 의도적으로 하드코딩한 부분

- 시연용 회원 5명의 이름과 건강 수치
- 랜딩 페이지 문구와 기능 소개
- 회원·전문가 사이드바 메뉴
- 데모 계정의 이동 경로

이 프로젝트는 실제 의료기관과 연결하지 않는 시연 서비스이므로 더미데이터는 `src/data/mock`에 고정했습니다.

### 하드코딩하지 않은 핵심 로직

- 데이터 접근은 `services`로 분리
- 서버 상태와 캐시는 TanStack Query로 관리
- 페이지 이동은 React Router로 관리
- 반복되는 카드와 상태 UI는 공통 컴포넌트로 분리
- API 응답과 화면 데이터 차이는 mapper 함수로 변환

면접에서는 다음처럼 설명할 수 있습니다.

> 시연 데이터는 의도적으로 하드코딩했지만 UI 컴포넌트가 더미데이터를 직접 참조하지 않게 했습니다. Promise 기반 mock service와 REST API service가 같은 메서드 형태를 가지므로 환경변수만 바꿔 실제 백엔드로 전환할 수 있습니다.

## 3. 꼭 이해할 핵심 파일

| 파일 | 공부할 내용 |
|---|---|
| `src/app/router.jsx` | URL과 페이지 컴포넌트의 연결 |
| `src/app/providers.jsx` | QueryClient가 한 번만 생성되어야 하는 이유 |
| `src/hooks/useDashboardData.js` | queryKey와 queryFn의 역할 |
| `src/services/healthService.js` | mock/API 구현 교체 방식 |
| `src/services/apiService.js` | fetch 공통 처리와 DTO 변환 |
| `src/pages/member/HealthRecordsPage.jsx` | 조회, 등록, 캐시 갱신, 폼 검증 |

## 4. 건강 기록 등록 흐름

```text
사용자가 폼 제출
  → required/min/max 검증
  → FormData로 값 읽기
  → 문자열을 Number로 변환
  → mutation.mutate()
  → service.addRecord()
  → 성공 시 records 캐시 무효화
  → 목록 자동 재조회
```

`useMutation`은 데이터를 생성·수정·삭제할 때 사용합니다. 등록 성공 후 `invalidateQueries`를 호출하는 이유는 화면의 기존 목록이 오래된 캐시이기 때문입니다.

## 5. 직접 코딩하며 공부할 순서

1. 새로운 메뉴 URL을 Router에 추가합니다.
2. mockService에 조회 메서드를 작성합니다.
3. 해당 메서드를 호출하는 Query Hook을 만듭니다.
4. loading, empty, error 상태를 먼저 구현합니다.
5. 데이터를 카드나 목록으로 렌더링합니다.
6. 마지막에 실제 Spring Boot API와 연결합니다.

주석의 `[발표 핵심]` 표시를 검색하면 우선 학습할 코드만 빠르게 찾을 수 있습니다.
