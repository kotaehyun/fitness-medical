package com.fitnessmedical.repository;

import com.fitnessmedical.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Member Entity의 데이터 접근 계층입니다.
 *
 * <p>JpaRepository&lt;Member, Long&gt;에서 Member는 관리할 Entity,
 * Long은 기본키 타입입니다. 인터페이스만 선언해도 Spring Data JPA가
 * findAll(), findById(), save(), delete() 구현체를 자동으로 만들어 줍니다.</p>
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
}
