package com.fitnessmedical.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 30)
    private String author;

    @Column(nullable = false, length = 50)
    private String role;

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
