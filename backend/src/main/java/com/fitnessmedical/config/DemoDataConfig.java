package com.fitnessmedical.config;

import com.fitnessmedical.entity.Feedback;
import com.fitnessmedical.entity.HealthRecord;
import com.fitnessmedical.entity.Member;
import com.fitnessmedical.entity.MemberStatus;
import com.fitnessmedical.repository.FeedbackRepository;
import com.fitnessmedical.repository.HealthRecordRepository;
import com.fitnessmedical.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.time.LocalDate;
import java.util.List;

/**
 * local 프로필에서만 사용하는 시연 데이터 설정입니다.
 * 운영 DB에는 가상 데이터가 자동으로 입력되지 않습니다.
 */
@Configuration
@Profile("local")
public class DemoDataConfig {

    @Bean
    // CommandLineRunner는 Spring Boot 시작이 끝난 뒤 한 번 실행됩니다.
    CommandLineRunner insertDemoData(MemberRepository memberRepository,
                                     HealthRecordRepository healthRecordRepository,
                                     FeedbackRepository feedbackRepository) {
        return args -> {
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
                            MemberStatus.CHECK_REQUIRED, LocalDate.of(2026, 7, 16))
            ));

            // 저장 후 반환된 첫 번째 회원은 DB에서 생성된 id를 가지고 있습니다.
            Member kimSunja = members.getFirst();
            int[] changes = {0, 1, -1, 2, 0, -2, 1, -1, 0, 2, -1, 1, 0, -2, 2,
                    1, -1, 0, 1, -2, 0, 2, -1, 1, 0, -1, 2, 0, -2, 0};

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
                        7500 + change * 260 + (i % 3) * 120
                ));
            }

            feedbackRepository.saveAll(List.of(
                    new Feedback(kimSunja, "홍길동", "재활의학 전문가", LocalDate.of(2026, 7, 18),
                            "최근 기록이 안정적으로 이어지고 있습니다. 무릎에 부담이 없는 범위에서 걷기 시간을 천천히 늘려보세요."),
                    new Feedback(kimSunja, "김길명", "운동 전문가", LocalDate.of(2026, 7, 16),
                            "이번 주 운동 목표를 잘 지키고 있어요. 다음 운동에서는 스트레칭 시간을 5분 더 확보해 보세요.")
            ));
        };
    }
}
