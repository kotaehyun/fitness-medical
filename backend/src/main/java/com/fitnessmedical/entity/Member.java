package com.fitnessmedical.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 회원 정보를 저장하는 JPA Entity입니다.
 * Entity는 데이터베이스 테이블과 연결되는 객체입니다.
 */
@Entity
@Table(name = "members")
public class Member {

    // @Id는 기본키(PK), IDENTITY는 DB가 번호를 자동 증가시키는 전략입니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable=false는 DB의 NOT NULL, length는 VARCHAR 길이로 반영됩니다.
    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(nullable = false)
    private int age;

    private double height;
    private double weight;

    @Column(nullable = false, length = 100)
    private String goal;

    private int progress;

    // EnumType.STRING을 사용하면 GOOD 같은 이름이 DB에 저장됩니다.
    // 순서 번호를 저장하는 ORDINAL보다 값 변경에 안전합니다.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    private LocalDate lastMeasuredDate;

    // JPA가 조회 결과로 객체를 만들 때 기본 생성자가 필요합니다.
    // 외부에서 의미 없이 new Member()를 호출하지 못하도록 protected로 제한합니다.
    protected Member() {
    }

    // 회원을 새로 등록할 때 사용할 생성자입니다.
    // id는 데이터베이스가 생성하므로 생성자 매개변수에 넣지 않습니다.
    public Member(String name, String gender, int age, double height, double weight,
                  String goal, int progress, MemberStatus status, LocalDate lastMeasuredDate) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.goal = goal;
        this.progress = progress;
        this.status = status;
        this.lastMeasuredDate = lastMeasuredDate;
    }

    // Entity의 상태를 외부에 전달하기 위한 Getter입니다.
    // Setter를 무조건 만들지 않고, 나중에 changeGoal()처럼 의미 있는 변경 메서드를 만드는 편이 좋습니다.
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public int getAge() { return age; }
    public double getHeight() { return height; }
    public double getWeight() { return weight; }
    public String getGoal() { return goal; }
    public int getProgress() { return progress; }
    public MemberStatus getStatus() { return status; }
    public LocalDate getLastMeasuredDate() { return lastMeasuredDate; }

    // 목표와 진행률을 함계 변경합니다.
    // 값 하나하나를 여는 Setter 대신 "무엇을 하는 변경인지"가 드러나는 이름을 씁니다.
    public void changeGoal(String goal, int progress) {
        this.goal = goal;
        this.progress = progress;
    }



}
