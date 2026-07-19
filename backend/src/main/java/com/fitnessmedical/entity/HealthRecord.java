package com.fitnessmedical.entity;

import com.fitnessmedical.entity.Member;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 회원이 날짜별로 입력한 건강 기록 Entity입니다.
 * 한 명의 회원은 여러 개의 건강 기록을 가질 수 있습니다.
 */
@Entity
@Table(name = "health_records")
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 여러 건강 기록이 한 회원을 참조하므로 다대일(ManyToOne) 관계입니다.
    // LAZY는 실제로 member 데이터가 필요할 때 조회하도록 지연 로딩합니다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // health_records 테이블의 member_id가 members 테이블의 PK를 참조합니다.
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private LocalDate measuredDate;

    private int systolic;
    private int diastolic;
    private int bloodSugar;
    private double weight;
    private double bodyFat;
    private double sleepHours;
    private int steps;

    protected HealthRecord() {
    }

    public HealthRecord(Member member, LocalDate measuredDate, int systolic, int diastolic,
                        int bloodSugar, double weight, double bodyFat, double sleepHours, int steps) {
        this.member = member;
        this.measuredDate = measuredDate;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.bloodSugar = bloodSugar;
        this.weight = weight;
        this.bodyFat = bodyFat;
        this.sleepHours = sleepHours;
        this.steps = steps;
    }

    public Long getId() { return id; }
    public Member getMember() { return member; }
    public LocalDate getMeasuredDate() { return measuredDate; }
    public int getSystolic() { return systolic; }
    public int getDiastolic() { return diastolic; }
    public int getBloodSugar() { return bloodSugar; }
    public double getWeight() { return weight; }
    public double getBodyFat() { return bodyFat; }
    public double getSleepHours() { return sleepHours; }
    public int getSteps() { return steps; }
}
