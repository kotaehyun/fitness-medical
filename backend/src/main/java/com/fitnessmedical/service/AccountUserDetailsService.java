package com.fitnessmedical.service;


import com.fitnessmedical.entity.Account;
import com.fitnessmedical.repository.AccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public AccountUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

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
