# Fitness Medical Database

기존 MariaDB와 학습용 MySQL을 서로 다른 포트로 실행합니다.

> **포트는 환경마다 다를 수 있습니다.**  
> 아래 표의 `3308`은 **RTX 4090 노트북(Windows)** 기준입니다.  
> 이 머신에서는 로컬 `mysqld`가 이미 `3307`을 쓰므로 Fitness Medical Docker MySQL만 `3308`로 매핑합니다.  
> Mac 등 다른 환경에서는 보통 `3307`을 쓰거나, 그 환경의 포트 충돌에 맞게 `docker-compose.yml`과 `DB_URL`을 조정하세요.

| DB | 실행 방식 | 포트 | 용도 / 환경 |
|---|---|---:|---|
| MariaDB | Homebrew / 로컬 | 3306 | 기존 로컬 프로젝트 |
| 로컬 mysqld | Windows 서비스 등 | 3307 | RTX 4090 노트북에 이미 점유됨 |
| MySQL 8.4 | Docker | **3308** | Fitness Medical (**RTX 4090 노트북**) |

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

RTX 4090 노트북 기본값:

```text
Host: localhost
Port: 3308
Database: fitness_medical
User: fitness_user
Password: fitness_dev_2026
```

다른 PC에서는 `docker-compose.yml`의 `ports`와 `application-mysql.yml`(또는 `DB_URL`)을 같은 포트로 맞추면 됩니다.

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
