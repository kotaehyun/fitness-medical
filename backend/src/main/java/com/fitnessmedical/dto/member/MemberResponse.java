package com.fitnessmedical.dto.member;

import com.fitnessmedical.entity.Member;
import java.time.LocalDate;

/**
 * [공부/면접] 회원 조회 응답 DTO
 *
 * <p><b>Q. Entity를 그대로 응답하면 안 되나?</b><br>
 * A. DB 컬럼·연관관계·영속성 컨텍스트와 API가 강결합됩니다.
 * DTO는 프론트에 필요한 필드만, status는 enum 이름 대신 한글 label로 변환합니다.</p>
 *
 * <p><b>Q. Java record란?</b><br>
 * A. Java 16+ 불변 데이터 클래스. 컴포넌트마다 접근자·equals/hashCode 자동 생성.
 * Response DTO처럼 값 전달용에 적합합니다.</p>
 */
public record MemberResponse(
        Long id,
        String name,
        String gender,
        int age,
        double height,
        double weight,
        String goal,
        int progress,
        String status,              // MemberStatus.getLabel() — "양호" 등 한글
        LocalDate lastMeasuredDate
) {
    // Entity → DTO 변환. LAZY 연관 객체 추가 로딩 없이 Member 필드만 사용
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(), member.getName(), member.getGender(), member.getAge(),
                member.getHeight(), member.getWeight(), member.getGoal(), member.getProgress(),
                member.getStatus().getLabel(), member.getLastMeasuredDate()
        );
    }
}
