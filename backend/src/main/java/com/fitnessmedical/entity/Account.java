package com.fitnessmedical.entity;

import jakarta.persistence.*;



@Entity
@Table(

        name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_accounts_login_id",
                        columnNames = "login_id"
                )
        }

)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="login_id", nullable = false, length = 50)
    private String loginId;

    // 평문 비밀번호가 아니라 BCrypt로 암호화된 값만 저장합니다.
    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "display_name", nullable = false, length = 30)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountRole role;

    protected Account() {
    }

    public Account(
            String loginId,
            String password,
            String displayName,
            AccountRole role
    ) {
        this.loginId = loginId;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AccountRole getRole() {
        return role;
    }
}
