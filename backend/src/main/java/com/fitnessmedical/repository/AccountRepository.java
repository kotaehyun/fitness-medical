package com.fitnessmedical.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fitnessmedical.entity.Account;

/**
 * [공부/면접] Account Entity용 Spring Data JPA Repository
 *
 * <p><b>Q. JpaRepository&lt;Account, Long&gt; 의미는?</b><br>
 * A. Account Entity, PK 타입 Long. save/findById/delete 등 CRUD는 인터페이스만으로 제공.</p>
 *
 * <p><b>Q. findByLoginId가 Optional을 반환하는 이유?</b><br>
 * A. 없을 수 있는 단건 조회 — {@code orElseThrow()}로 명시적 처리.
 * null 반환보다 NPE·의도 불명확성을 줄입니다.</p>
 *
 * <p><b>Q. existsByMember_Id 문법은?</b><br>
 * A. Account.member(Member).id 경로 탐색. {@code _Id}는 member_id FK로
 * "이 회원에 이미 계정이 연결됐는지" EXISTS 쿼리 생성.
 * MEMBER 가입 시 중복 연결 방지에 사용.</p>
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Query Method → SELECT ... WHERE login_id = ?
    Optional<Account> findByLoginId(String loginId);

    // boolean exists → COUNT &gt; 0 최적화 가능
    boolean existsByLoginId(String loginId);

    // member_id FK 중복 검사 — MEMBER 역할 계정 1:1 제약
    boolean existsByMember_Id(Long memberId);

    // 전문의 면허번호 중복 가입 방지
    boolean existsByLicenseNumber(String licenseNumber);
}
