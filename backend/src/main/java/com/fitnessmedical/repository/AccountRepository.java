package com.fitnessmedical.repository;

import com.fitnessmedical.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Account = 관리할 Entity
// Long = Account의 PK 타입
// JpaRepository = save, findById, findAll, delete 등 기본 CRUD 제공
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
