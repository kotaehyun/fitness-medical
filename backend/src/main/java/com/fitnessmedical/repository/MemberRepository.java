package com.fitnessmedical.repository;

import com.fitnessmedical.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * [공부/면접] Member Entity용 Spring Data JPA Repository
 *
 * <p><b>Q. Repository vs Service?</b><br>
 * A. Repository는 DB CRUD·쿼리만. 비즈니스 규칙(기본 status 설정 등)은 Service.</p>
 *
 * <p><b>Q. 커스텀 메서드 없이 JpaRepository만 상속해도 되나?</b><br>
 * A. findAll/findById/save/delete로 CRUD 충분할 때는 선언만으로 구현체 자동 생성.</p>
 *
 * <p><b>Q. findById 반환 타입 Optional&lt;Member&gt;?</b><br>
 * A. Spring Data JPA 2.x+ 기본. 없으면 empty → Service에서 orElseThrow로 404 처리.</p>
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
}
