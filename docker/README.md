# Fitness Medical Database

기존 MariaDB와 학습용 MySQL을 서로 다른 포트로 실행합니다.

| DB | 실행 방식 | 포트 | 용도 |
|---|---|---:|---|
| MariaDB | Homebrew Service | 3306 | 기존 로컬 프로젝트 |
| MySQL 8.4 | Docker | 3307 | Fitness Medical |

## MySQL 실행

```bash
cd docker
docker compose up -d mysql
```

## 상태 확인

```bash
docker compose ps
docker logs fitness-medical-mysql
```

## 접속 정보

```text
Host: localhost
Port: 3307
Database: fitness_medical
User: fitness_user
Password: fitness_dev_2026
```

터미널 접속:

```bash
docker exec -it fitness-medical-mysql \
  mysql -u fitness_user -p fitness_medical
```

## 종료

```bash
docker compose stop mysql
```

`docker compose down`은 컨테이너를 제거하지만 named volume은 유지합니다. `down -v`는 DB 데이터까지 삭제하므로 학습 데이터를 지울 의도가 있을 때만 사용합니다.
