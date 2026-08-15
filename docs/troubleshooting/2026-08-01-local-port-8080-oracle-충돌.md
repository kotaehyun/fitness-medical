# 로컬 8080 포트가 Oracle XML DB와 충돌함 (by-taehyun 기기)

## 증상

`./gradlew bootRun`으로 백엔드를 띄운 뒤 `POST http://localhost:8080/api/members`로 요청을 보내면 다음처럼 401이 응답됨.

```text
HTTP/1.1 401 Unauthorized
Server: Oracle XML DB/Oracle Database
WWW-Authenticate: Basic realm="XDB"

<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN">
<HTML><HEAD><TITLE>401 Unauthorized</TITLE></HEAD>
<BODY><H1>Unauthorized</H1></BODY></HTML>
```

`SecurityConfig.java`에서 이미 `/api/**`를 `permitAll()`로 열어뒀는데도 401이 나서 처음엔 Spring Security 설정 문제로 오해했음.

## 원인

응답 헤더의 `Server: Oracle XML DB/Oracle Database`가 결정적 단서. **이 PC에 설치된 Oracle Database가 8080 포트에서 자체 HTTP/WebDAV 리스너(XML DB, XDB)를 띄우고 있어서, curl 요청이 Spring Boot 앱이 아니라 Oracle로 갔던 것.** Spring Boot 코드/설정 문제가 아니었음.

## 해결

`application.yml`에 이미 `server.port: ${SERVER_PORT:8080}`로 환경변수 오버라이드가 되어 있어서, 코드 수정 없이 실행 시 포트만 바꾸면 됨.

```bash
SERVER_PORT=8081 ./gradlew bootRun
```

테스트도 8081로:

```bash
curl -X POST http://localhost:8081/api/members \
  -H "Content-Type: application/json" \
  -d '{"name":"홍길동","gender":"남","age":30,"height":175,"weight":70,"goal":"체중 감량","progress":0}'
```

## 참고

- 프론트엔드 `.env`에서 `VITE_API_URL`을 백엔드와 연동할 때도 `http://localhost:8081/api`로 맞춰야 함 (이 기기에 한해서).
- 근본적으로 해결하려면 Oracle의 XDB HTTP 포트를 바꾸거나(`EXEC DBMS_XDB.SETHTTPPORT(0);`로 비활성화 등, SYSDBA 권한 필요) Oracle을 안 쓸 때는 관련 서비스를 꺼두는 방법도 있음 — 지금은 매번 `SERVER_PORT` 환경변수로 우회하는 쪽을 택함.
