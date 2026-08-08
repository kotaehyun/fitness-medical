package com.fitnessmedical.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * [공부/면접] 회원별 건강 기록 JPA Entity
 *
 * <p><b>Q. Account–Member @OneToOne과 HealthRecord–Member @ManyToOne 차이는?</b><br>
 * A. Account↔Member는 로그인 계정 1개당 회원 1명(1:1).
 * HealthRecord↔Member는 한 회원이 날짜별로 여러 기록(1:N, owning side는 HealthRecord).</p>
 *
 * <p><b>Q. FetchType.LAZY를 쓰는 이유는?</b><br>
 * A. member를 항상 JOIN하지 않고, {@code getMember()} 호출 시에만 로딩합니다.
 * 트랜잭션 밖에서 LAZY 접근 시 LazyInitializationException 주의.</p>
 *
 * <p><b>Q. Entity vs HealthRecordRequest DTO?</b><br>
 * A. DTO는 memberId를 URL에서 받고 측정값만 검증합니다.
 * Entity는 {@link Member} 객체 참조와 DB PK(id)를 갖습니다.</p>
 */
@Entity
@Table(name = "health_records")
public class HealthRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 다대일: 여러 HealthRecord → 하나의 Member
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    // 측정일 — 같은 회원·같은 날짜 중복 여부는 Service/Repository에서 검증
    @Column(nullable = false)
    private LocalDate measuredDate;

    private int systolic;    // 수축기 혈압
    private int diastolic;   // 이완기 혈압
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
