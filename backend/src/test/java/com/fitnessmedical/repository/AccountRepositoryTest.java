package com.fitnessmedical.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.entity.AccountRole;
import com.fitnessmedical.entity.ProfessionalType;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * [공부/면접] STUDY_TASKS 6단계 — Repository 테스트
 *
 * Q. @DataJpaTest 는?
 * A. JPA 슬라이스. Entity·Repository만 띄우고 임베디드 H2에 실제 SQL을 실행한다.
 *    mock Repository를 쓰는 AccountServiceTest와 다르다.
 *
 * 직접 구현: 아래 세 테스트 본문. 완성 코드는 주석에도 두지 않는다.
 * 실행: ./gradlew test --tests AccountRepositoryTest
 */
@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("저장한 loginId로 계정을 다시 찾을 수 있다.")
    void findByLoginId_savedAccount_isPresent() {
        // region [직접구현]
        Account account = new Account("test123", "password123", "test123", AccountRole.MEMBER, null);
        accountRepository.save(account);

        // endregion
        Optional<Account> result = accountRepository.findByLoginId(account.getLoginId());
        assertThat(result).isPresent();
        assertThat(result.get().getLoginId()).isEqualTo(account.getLoginId());
        assertThat(result.get().getPassword()).isEqualTo(account.getPassword());
        assertThat(result.get().getDisplayName()).isEqualTo(account.getDisplayName());
        assertThat(result.get().getRole()).isEqualTo(account.getRole());
        assertThat(result.get().getProfessionalType()).isNull();
        assertThat(result.get().getLicenseNumber()).isNull();
        assertThat(result.get().isProfessionalVerified()).isFalse();
        assertThat(result.get().getMember()).isNull();
    }

    @Test
    @DisplayName("없는 loginId는 existsByLoginId 가 false 이다.")
    void existsByLoginId_unknown_isFalse() {
        // region [직접구현]
        boolean exists = accountRepository.existsByLoginId("여기에없는아이디");

        // endregion
        assertThat(exists).isFalse();

    }

    @Test
    @DisplayName("같은 면허번호를 저장하면 existsByLicenseNumber 가 true 이다.")
    void existsByLicenseNumber_afterSave_isTrue() {
        // region [직접구현]
        Account account = new Account(
            "로그인 아이디",
            "비밀번호",
            "표시이름",
            AccountRole.PROFESSIONAL,
            null,
            ProfessionalType.PHYSICIAN,
            "면허번호",
            false
        );

        // endregion
        accountRepository.save(account);

        boolean exists = accountRepository.existsByLicenseNumber(account.getLicenseNumber());
        assertThat(exists).isTrue();
    }
}
