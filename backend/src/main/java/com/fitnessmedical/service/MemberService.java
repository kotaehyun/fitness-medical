package com.fitnessmedical.service;

import com.fitnessmedical.common.ResourceNotFoundException;
import com.fitnessmedical.dto.member.MemberResponse;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 회원과 관련된 비즈니스 로직을 담당합니다.
 * Controller가 Repository를 직접 호출하지 않고 Service를 거치게 구성합니다.
 */
@Service
// 기본 조회 메서드에서는 데이터가 변경되지 않도록 읽기 전용 트랜잭션을 사용합니다.
@Transactional(readOnly = true)
public class MemberService {

    // final 필드는 생성자에서 한 번만 할당할 수 있습니다.
    private final MemberRepository memberRepository;

    // 생성자 주입 방식입니다.
    // Spring이 MemberRepository 구현체를 찾아 이 생성자의 매개변수로 전달합니다.
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberResponse> findAll() {
        // 1. Repository에서 Entity 목록을 조회합니다.
        // 2. stream과 map으로 각 Entity를 응답 DTO로 변환합니다.
        // 3. toList()로 새로운 List를 만듭니다.
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
    }

    public MemberResponse findById(Long id) {
        return MemberResponse.from(getMember(id));
    }

    public Member getMember(Long id) {
        // findById의 결과는 값이 없을 수도 있으므로 Optional<Member>입니다.
        // 값이 없으면 orElseThrow를 통해 공통 404 예외를 발생시킵니다.
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
    }
}
