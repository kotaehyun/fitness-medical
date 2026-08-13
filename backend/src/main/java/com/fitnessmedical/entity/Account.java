package com.fitnessmedical.entity;

import jakarta.persistence.*;

/**
 * [공부/면접] 로그인 계정 JPA Entity
 *
 * <p>Entity는 DB 테이블({@code accounts})과 1:1로 매핑되는 영속 객체입니다.
 * DTO(AccountCreateRequest 등)와 달리 JPA가 INSERT/UPDATE/SELECT를 관리합니다.</p>
 *
 * <p><b>Q. Entity와 DTO의 차이는?</b><br>
 * A. Entity는 DB 스키마·연관관계·영속성 컨텍스트와 연결되고,
 * DTO는 API 입출력 전용으로 비즈니스 규칙 검증(@NotBlank 등)과
 * 민감 필드 제외(비밀번호)를 담당합니다.</p>
 *
 * <p><b>Q. Account–Member @OneToOne 관계는?</b><br>
 * A. {@link AccountRole#MEMBER} 계정만 {@link Member}와 1:1로 연결됩니다.
 * {@code member_id} FK가 {@code unique}이므로 한 회원당 계정은 최대 1개입니다.
 * {@link AccountRole#PROFESSIONAL}·{@link AccountRole#ADMIN}은 {@code member == null}이어야 합니다.</p>
 *
 * <p><b>Q. password·면허번호를 로그·응답에 넣어도 되나?</b><br>
 * A. 절대 안 됩니다. {@link AccountResponse}에도 password·licenseNumber는 포함하지 않습니다.</p>
 */
@Entity
@Table(
        name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_accounts_login_id",
                        columnNames = "login_id"
                ),
                @UniqueConstraint(
                        name = "uk_accounts_license_number",
                        columnNames = "license_number"
                )
        }
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인 식별자 — DB UNIQUE 제약으로 중복 가입 방지
    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;

    // 평문 비밀번호가 아니라 BCrypt로 암호화된 값만 저장합니다. 로깅·응답 금지.
    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "display_name", nullable = false, length = 30)
    private String displayName;

    // EnumType.STRING → DB에 "MEMBER"/"PROFESSIONAL"/"ADMIN" 문자열 저장 (ORDINAL보다 안전)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "professional_type", length = 20)
    private ProfessionalType professionalType;

    // 전문의 면허 또는 트레이너 자격번호 — 로깅·응답 금지. UNIQUE(NULL은 여러 행 허용)
    @Column(name = "license_number", length = 20)
    private String licenseNumber;

    @Column(name = "professional_verified", nullable = false)
    private boolean professionalVerified;

    // MEMBER 역할일 때만 Member와 1:1 연결. PROFESSIONAL·ADMIN은 null.
    // LAZY: member 필드 접근 시점까지 JOIN 지연 → N+1 주의, 트랜잭션 내 접근 권장
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    // JPA 스펙상 protected 기본 생성자 필요 (리플렉션으로 객체 생성)
    protected Account() {
    }

    public Account(
            String loginId,
            String password,
            String displayName,
            AccountRole role,
            Member member
    ) {
        this(loginId, password, displayName, role, member, null, null, false);
    }

    public Account(
            String loginId,
            String password,
            String displayName,
            AccountRole role,
            Member member,
            ProfessionalType professionalType,
            String licenseNumber,
            boolean professionalVerified
    ) {
        this.loginId = loginId;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
        this.member = member;
        this.professionalType = professionalType;
        this.licenseNumber = licenseNumber;
        this.professionalVerified = professionalVerified;
    }

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    // 인증(BCrypt matches)에만 사용. 절대 로그·JSON 응답에 노출하지 않음
    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AccountRole getRole() {
        return role;
    }

    public ProfessionalType getProfessionalType() {
        return professionalType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public boolean isProfessionalVerified() {
        return professionalVerified;
    }

    // 관리자 승인/해제 전용. 공개 가입은 항상 false로 저장된다.
    public void setProfessionalVerified(boolean professionalVerified) {
        this.professionalVerified = professionalVerified;
    }

    public Member getMember() {
        return member;
    }
}
