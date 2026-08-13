package com.fitnessmedical.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * [공부/면접] 전문가 피드백 JPA Entity
 *
 * <p><b>Q. FeedbackRequest DTO와 Entity 차이는?</b><br>
 * A. DTO는 content만 받는다. memberId는 URL, writtenDate·author·role은 서버(세션 Account).
 * Entity는 {@link Member} FK·PK·작성일 등 DB 영속 필드를 모두 보유합니다.</p>
 *
 * <p><b>Q. author·role을 String으로 두는 이유는?</b><br>
 * A. 피드백 작성 시점의 표시명·직함 스냅샷(회원/계정 테이블과 느슨한 결합).</p>
 */
@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 피드백 대상 회원 — HealthRecord와 동일하게 @ManyToOne
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 30)
    private String author;

    @Column(nullable = false, length = 50)
    private String role;

    // 등록일 — FeedbackRequest에 없고 Service에서 LocalDate.now()로 설정
    @Column(nullable = false)
    private LocalDate writtenDate;

    @Column(nullable = false, length = 1000)
    private String content;

    protected Feedback() {
    }

    public Feedback(Member member, String author, String role, LocalDate writtenDate, String content) {
        this.member = member;
        this.author = author;
        this.role = role;
        this.writtenDate = writtenDate;
        this.content = content;
    }

    public Long getId() { return id; }
    public Member getMember() { return member; }
    public String getAuthor() { return author; }
    public String getRole() { return role; }
    public LocalDate getWrittenDate() { return writtenDate; }
    public String getContent() { return content; }
}
