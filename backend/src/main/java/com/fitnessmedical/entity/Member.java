package com.fitnessmedical.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * [공부/면접] 회원 정보 JPA Entity
 *
 * <p><b>Q. Entity와 DTO(MemberCreateRequest)의 차이는?</b><br>
 * A. Entity는 DB {@code members} 테이블과 매핑되고 JPA가 생명주기를 관리합니다.
 * DTO는 가입·수정 API의 입력 검증용이며, {@code status}·{@code lastMeasuredDate}처럼
 * 서버가 기본값을 정하는 필드는 DTO에 넣지 않습니다.</p>
 *
 * <p><b>Q. Account와의 관계는?</b><br>
 * A. {@link Account} 쪽이 {@code @OneToOne} owning side({@code member_id} FK 보유).
 * Member Entity에는 Account 역참조 필드가 없어도 FK는 accounts 테이블에 존재합니다.</p>
 *
 * <p><b>Q. Setter 대신 changeGoal()을 쓰는 이유는?</b><br>
 * A. 무분별한 setter는 도메인 불변식을 깨기 쉽습니다.
 * "목표 변경"처럼 의도가 드러나는 메서드가 유지보수에 유리합니다.</p>
 */
@Entity
@Table(name = "members")
public class Member {

    // @Id = PK, IDENTITY = DB AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    // EnumType.STRING → "GOOD", "CAUTION" 등 이름이 DB에 저장됨
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    // 최근 측정일 — 건강 기록 등록 전에는 null 가능
    private LocalDate lastMeasuredDate;

    // JPA가 조회 결과로 객체를 만들 때 기본 생성자 필요. protected로 무분별한 new 방지
    protected Member() {
    }

    // id는 DB가 생성하므로 생성자 매개변수에 포함하지 않음
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

    // 목표와 진행률을 함께 변경 — MemberUpdateRequest → Service → 이 메서드 호출 흐름
    public void changeGoal(String goal, int progress) {
        this.goal = goal;
        this.progress = progress;
    }
}
