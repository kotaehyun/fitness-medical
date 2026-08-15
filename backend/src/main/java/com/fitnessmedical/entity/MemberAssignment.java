package com.fitnessmedical.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * [공부/면접] 회원 담당 전문의·트레이너
 *
 * Q. 왜 채팅 전에 이 테이블이 있나?
 * A. 목록의 「담당」표시와 1:1 대화 상대가 같은 선이다.
 *    인증된 PROFESSIONAL 전원과 대화하면 소유권 설명이 깨진다.
 *
 * Q. 협진 기록인가?
 * A. 아니다. 담당 연결만 둔다. 진단·처방 테이블이 아니다.
 */
@Entity
@Table(
        name = "member_assignments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_member_assignments_member",
                columnNames = "member_id"
        )
)
public class MemberAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physician_account_id")
    private Account physician;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_account_id")
    private Account trainer;

    protected MemberAssignment() {
    }

    public MemberAssignment(Member member, Account physician, Account trainer) {
        this.member = member;
        this.physician = physician;
        this.trainer = trainer;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Account getPhysician() {
        return physician;
    }

    public Account getTrainer() {
        return trainer;
    }
}
