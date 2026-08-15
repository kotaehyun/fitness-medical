package com.fitnessmedical.config;

import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.entity.Feedback;
import com.fitnessmedical.entity.HealthRecord;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.entity.MemberAssignment;
import com.fitnessmedical.entity.MemberStatus;
import com.fitnessmedical.entity.ProfessionalType;
import com.fitnessmedical.repository.AccountRepository;
import com.fitnessmedical.repository.FeedbackRepository;
import com.fitnessmedical.repository.HealthRecordRepository;
import com.fitnessmedical.repository.MemberAssignmentRepository;
import com.fitnessmedical.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.List;

/**
 * [공부/면접] 로컬 시연용 데이터 (@Profile + CommandLineRunner)
 *
 * Q. @Profile("local")을 쓰는 이유?
 * A. spring.profiles.active=local 일 때만 이 @Configuration Bean이 등록된다.
 *    운영(prod) DB에 시연 회원·건강 기록이 자동 삽입되는 사고를 막는다.
 *
 * Q. CommandLineRunner vs ApplicationRunner?
 * A. 둘 다 ApplicationContext 준비·run() 직후 한 번 실행된다.
 *    CommandLineRunner는 String[] args, ApplicationRunner는 ApplicationArguments를 받는다.
 *
 * Q. saveAll 후 getFirst()로 kimSunja를 쓰는 이유?
 * A. JPA saveAll은 영속화된 Entity(id 포함)를 반환한다.
 *    FK(HealthRecord.member, Feedback.member)에 DB가 부여한 id가 필요하므로 저장 후 참조한다.
 *
 * Q. 건강 수치·피드백 문구 주의?
 * A. UI·차트 시연용 샘플 데이터이며, 실제 건강 관리·운동 지침을 대체하지 않는다.
 *
 * local 프로필에서만 사용하는 시연 데이터 설정입니다.
 * 운영 DB에는 가상 데이터가 자동으로 입력되지 않습니다.
 */
@Configuration
@Profile("local")
public class DemoDataConfig {

        @Bean
        @SuppressWarnings("null")
        // Spring Boot 기동 완료 후 1회 실행 — Repository가 준비된 뒤 INSERT하기 위함
        public CommandLineRunner insertDemoData(MemberRepository memberRepository,
                        HealthRecordRepository healthRecordRepository,
                        FeedbackRepository feedbackRepository,
                        AccountRepository accountRepository,
                        MemberAssignmentRepository memberAssignmentRepository,
                        PasswordEncoder passwordEncoder) {
                return args -> {
                        if (accountRepository.existsByLoginId("member01")
                            || accountRepository.existsByLoginId("trainer01")
                            || accountRepository.existsByLoginId("doctor01")
                            || accountRepository.existsByLoginId("admin01")) {
                            ensureKimSunjaAssignment(
                                    memberRepository,
                                    accountRepository,
                                    memberAssignmentRepository
                            );
                            return;
                        }
                        // saveAll은 여러 Entity를 한 번에 저장합니다.
                        List<Member> members = memberRepository.saveAll(List.of(
                                        new Member("김순자", "여성", 56, 164, 68, "건강한 일상 복귀", 70,
                                                        MemberStatus.GOOD, LocalDate.of(2026, 7, 19)),
                                        new Member("이영희", "여성", 48, 160, 61.4, "생활 체력 높이기", 82,
                                                        MemberStatus.GOOD, LocalDate.of(2026, 7, 19)),
                                        new Member("박정호", "남성", 62, 173, 76.2, "규칙적인 걷기", 54,
                                                        MemberStatus.CAUTION, LocalDate.of(2026, 7, 18)),
                                        new Member("최민수", "남성", 39, 178, 81.8, "체중 균형 관리", 66,
                                                        MemberStatus.GOOD, LocalDate.of(2026, 7, 18)),
                                        new Member("한미정", "여성", 67, 158, 59.3, "관절 부담 줄이기", 43,
                                                        MemberStatus.CHECK_REQUIRED, LocalDate.of(2026, 7, 16))));

                        // 저장 후 반환된 첫 번째 회원은 DB에서 생성된 id를 가지고 있습니다.
                        Member kimSunja = members.getFirst();
                        // 30일치 차트 변화를 보이게 하기 위한 임의 변동값(시연용, 임상 데이터 아님)
                        int[] changes = { 0, 1, -1, 2, 0, -2, 1, -1, 0, 2, -1, 1, 0, -2, 2,
                                        1, -1, 0, 1, -2, 0, 2, -1, 1, 0, -1, 2, 0, -2, 0 };

                        // 반복문으로 최근 30일 건강 기록을 생성합니다.
                        for (int i = 0; i < 30; i++) {
                                int change = changes[i];
                                healthRecordRepository.save(new HealthRecord(
                                                kimSunja,
                                                LocalDate.of(2026, 7, 19).minusDays(i),
                                                120 + change,
                                                80 + Math.round(change / 2.0f),
                                                102 + change * 2,
                                                Math.round((68 + i * 0.018 + change * 0.03) * 10) / 10.0,
                                                Math.round((25.5 + change * 0.08) * 10) / 10.0,
                                                Math.round((7 + change * 0.12) * 10) / 10.0,
                                                7500 + change * 260 + (i % 3) * 120));
                        }

                        // 전문가 코멘트 샘플 — 앱 UI 시연용이며 개인별 건강 관리 조언을 대체하지 않음
                        feedbackRepository.saveAll(List.of(
                                        new Feedback(kimSunja, "홍길동", "재활의학 전문가", LocalDate.of(2026, 7, 18),
                                                        "최근 기록이 안정적으로 이어지고 있습니다. 무릎에 부담이 없는 범위에서 걷기 시간을 천천히 늘려보세요."),
                                        new Feedback(kimSunja, "김길명", "운동 전문가", LocalDate.of(2026, 7, 16),
                                                        "이번 주 운동 목표를 잘 지키고 있어요. 다음 운동에서는 스트레칭 시간을 5분 더 확보해 보세요.")));

                        String demoPassword = passwordEncoder.encode("password123");
                        accountRepository.saveAll(List.of(
                                        new Account(
                                                        "member01",
                                                        demoPassword,
                                                        "김순자",
                                                        AccountRole.MEMBER,
                                                        kimSunja
                                        ),
                                        new Account(
                                                        "trainer01",
                                                        demoPassword,
                                                        "김길명",
                                                        AccountRole.PROFESSIONAL,
                                                        null,
                                                        ProfessionalType.TRAINER,
                                                        "SP21001234",
                                                        true
                                        ),
                                        new Account(
                                                        "doctor01",
                                                        demoPassword,
                                                        "홍길동",
                                                        AccountRole.PROFESSIONAL,
                                                        null,
                                                        ProfessionalType.PHYSICIAN,
                                                        "123456",
                                                        true
                                        ),
                                        // 슈퍼계정 — 공개 가입 전문가의 professionalVerified 승인/해제
                                        new Account(
                                                        "admin01",
                                                        demoPassword,
                                                        "관리자",
                                                        AccountRole.ADMIN,
                                                        null
                                        )
                        ));
                        ensureKimSunjaAssignment(
                                memberRepository,
                                accountRepository,
                                memberAssignmentRepository
                        );
                };
        }

        private static void ensureKimSunjaAssignment(
                MemberRepository memberRepository,
                AccountRepository accountRepository,
                MemberAssignmentRepository memberAssignmentRepository
        ) {
                Member kim = memberRepository.findAll().stream()
                        .filter(member -> "김순자".equals(member.getName()))
                        .findFirst()
                        .orElse(null);
                Account doctor = accountRepository.findByLoginId("doctor01").orElse(null);
                Account trainer = accountRepository.findByLoginId("trainer01").orElse(null);
                if (kim == null || kim.getId() == null || doctor == null || trainer == null) {
                        return;
                }
                if (memberAssignmentRepository.findByMember_Id(kim.getId()).isPresent()) {
                        return;
                }
                memberAssignmentRepository.save(new MemberAssignment(kim, doctor, trainer));
        }
}
