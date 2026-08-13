-- 이 SQL은 ERD 학습을 위한 참고용입니다.
-- 실제 실행 시에는 JPA가 Entity를 기준으로 테이블을 생성합니다.

CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    age INT NOT NULL,
    height DOUBLE NOT NULL,
    weight DOUBLE NOT NULL,
    goal VARCHAR(100) NOT NULL,
    progress INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    last_measured_date DATE
);

CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    display_name VARCHAR(30) NOT NULL,
    role VARCHAR(20) NOT NULL,
    professional_type VARCHAR(20) NULL,
    license_number VARCHAR(20) NULL, -- 전문의 면허 또는 트레이너 자격번호
    professional_verified BOOLEAN NOT NULL DEFAULT FALSE,
    member_id BIGINT NULL,
    CONSTRAINT uk_accounts_login_id UNIQUE (login_id),
    CONSTRAINT uk_accounts_member_id UNIQUE (member_id),
    CONSTRAINT fk_accounts_member
        FOREIGN KEY (member_id) REFERENCES members(id)
);

CREATE TABLE health_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    measured_date DATE NOT NULL,
    systolic INT NOT NULL,
    diastolic INT NOT NULL,
    blood_sugar INT NOT NULL,
    weight DOUBLE NOT NULL,
    body_fat DOUBLE NOT NULL,
    sleep_hours DOUBLE NOT NULL,
    steps INT NOT NULL,
    CONSTRAINT fk_health_records_member
        FOREIGN KEY (member_id) REFERENCES members(id)
);

CREATE TABLE feedbacks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    author VARCHAR(30) NOT NULL,
    role VARCHAR(50) NOT NULL,
    written_date DATE NOT NULL,
    content VARCHAR(1000) NOT NULL,
    CONSTRAINT fk_feedbacks_member
        FOREIGN KEY (member_id) REFERENCES members(id)
);
