package com.fitnessmedical.dto.member;

import com.fitnessmedical.entity.Member;
import java.time.LocalDate;

/**
 * 회원 조회 결과를 프론트엔드에 전달하는 응답 DTO입니다.
 * Entity를 그대로 응답하면 DB 구조와 API가 강하게 연결되므로 DTO로 분리합니다.
 * record는 값 전달용 클래스를 짧게 작성할 수 있는 Java 문법입니다.
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
        String status,
        LocalDate lastMeasuredDate
) {
    // Entity를 MemberResponse DTO로 바꾸는 정적 팩토리 메서드입니다.
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(), member.getName(), member.getGender(), member.getAge(),
                member.getHeight(), member.getWeight(), member.getGoal(), member.getProgress(),
                member.getStatus().getLabel(), member.getLastMeasuredDate()
        );
    }
}
