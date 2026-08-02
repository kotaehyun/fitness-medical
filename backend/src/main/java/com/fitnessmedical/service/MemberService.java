package com.fitnessmedical.service;

import com.fitnessmedical.common.ResourceNotFoundException;
import com.fitnessmedical.dto.member.MemberCreateRequest;
import com.fitnessmedical.dto.member.MemberResponse;
import com.fitnessmedical.dto.member.MemberUpdateRequest;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.entity.MemberStatus;
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

    // 클래스 전체엔 @Transactional(readOnly = true)가 적용되어 있으니
    // 데이터를 저장(변경)하는 이 메서드는 개별 @Transactional을 다시 붙여서 덮어씁니다.
    @Transactional
    public MemberResponse create(MemberCreateRequest request) {

        // 1. 요청 DTO 값 7개 + 서버가 정하는 기본값 2개(status, lastMeasuredDate)로 Entity를 만듭니다.
        //    Member 생성자 순서: name, gender, age, height, weight, goal, progress, status, lastMeasuredDate
        Member member = new Member(
                request.name(), request.gender(), request.age(),
                request.height(), request.weight(), request.goal(), request.progress(),
                MemberStatus.CHECK_REQUIRED, // 서버 기본값: 아직 측정 이력이 없으니 "확인 필요"로 시작
                null                          // 서버 기본값: 최근 측정일은 아직 없음

        );

        // 2. save()가 INSERT를 수행하고, DB가 채운 id 포함한 Entity를 돌려줍니다.
        Member saved = memberRepository.save(member);

        // 3. 저장된 Entity를 응답 DTO로 바꿔서 반환합니다. (Entity를 그대로 반환하지 않는 이유는
        //    MemberResponse 파일 주석에 있던 것 기억나시죠 — DB 구조와 API를 분리하기 위해서예요.)
        return MemberResponse.from(saved);

    }

    @Transactional
    public MemberResponse update(Long memberId, MemberUpdateRequest request) {
        // 1. 기존 회원을 조회합니다. 없으면 getMember() 안에서 이미 404 예외를 던져줍니다.
        Member member = getMember(memberId);

        // 2. 조회된 Entity의 값을 바꿉니다.
        member.changeGoal(request.goal(), request.progress());

        // 3. 여기선 memberRepository.save()를 따로 호출하지 않습니다!
        //    @Transactional 메서드 안에서 조회한 Entity는 JPA가 "영속 상태"로 계속 감시하고 있어서,
        //    메서드가 끝나고 트랜잭션이 커밋되는 시점에 바뀐 값을 JPA가 알아서 UPDATE 쿼리로 반영해줍니다.
        //    이걸 "더티 체킹(dirty checking)"이라고 불러요. create()에서 save()를 호출했던 것과
        //    비교되는 부분이니 기억해두시면 좋아요.
        return MemberResponse.from(member);
    }

    // 클래스 전체엔 readOnly=true가 적용되어 있으니, 삭제하는 이 메서드도 개별 @Transactional이 필요합니다.
    @Transactional
    public void delete(Long memberId) {
        // 1. 기존 회원을 조회합니다. 없으면 getMember() 안에서 이미 404 예외를 던져줍니다.
        Member member = getMember(memberId);

        // 2. 조회한 Entity를 그대로 삭제합니다.
        //    JpaRepository가 기본 제공하는 delete(T entity) 메서드입니다.
        memberRepository.delete(member);
    }
}
