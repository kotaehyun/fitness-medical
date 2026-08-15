package com.fitnessmedical.service;


import com.fitnessmedical.entity.Account;
import com.fitnessmedical.repository.AccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * [공부/면접] Spring Security가 로그인 시 호출하는 UserDetailsService 구현체입니다.
 *
 * Q. UserDetailsService는 어디서 쓰이나요?
 * A. AuthenticationManager.authenticate()가 UsernamePasswordAuthenticationToken을 받으면
 *    내부에서 loadUserByUsername(loginId)로 DB 계정을 조회하고,
 *    PasswordEncoder.matches()로 제출된 비밀번호와 DB 해시를 비교합니다.
 *
 * Q. Account Entity를 그대로 반환하지 않고 UserDetails를 만드는 이유는?
 * A. Security 프레임워크는 UserDetails 인터페이스만 알고 있으며,
 *    역할(roles), 계정 잠금 등 Security 전용 필드를 표준 형태로 전달하기 위해서입니다.
 *
 * Q. 여기서 PasswordEncoder를 직접 쓰지 않는 이유는?
 * A. 비밀번호 검증은 DaoAuthenticationProvider가 UserDetails.password(해시)와
 *    클라이언트가 보낸 평문을 matches()로 비교합니다. 이 Service는 "조회"만 담당합니다.
 *
 * 계층: AuthController → AuthenticationManager → AccountUserDetailsService → AccountRepository
 */
@Service
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public AccountUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * [공부/면접] loginId(= username)로 계정을 찾아 Spring Security UserDetails로 변환합니다.
     *
     * 흐름: AccountRepository.findByLoginId → 없으면 UsernameNotFoundException
     *       → User.withUsername().password(해시).roles() 빌드
     *
     * 면접 포인트: .password()에는 DB에 저장된 해시만 넣습니다. 평문 비밀번호를
     *             로그에 남기거나 UserDetails에 평문을 넣으면 안 됩니다.
     *             roles()에는 AccountRole enum name이 ROLE_ 접두사와 함께 권한으로 등록됩니다.
     */
    @Override
    public UserDetails loadUserByUsername(String loginId)
            throws UsernameNotFoundException {

        Account account = accountRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "계정을 찾을 수 없습니다."
                ));

        return User.withUsername(account.getLoginId())
                .password(account.getPassword())
                .roles(account.getRole().name())
                .build();
    }
}
