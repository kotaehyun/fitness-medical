package com.fitnessmedical.service;


import com.fitnessmedical.common.DuplicateResourceException;
import com.fitnessmedical.common.InvalidCredentialsException;
import com.fitnessmedical.dto.account.AccountCreateRequest;
import com.fitnessmedical.dto.account.AccountLoginRequest;
import com.fitnessmedical.dto.account.AccountResponse;
import com.fitnessmedical.entity.Account;
import com.fitnessmedical.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AccountResponse create(AccountCreateRequest request) {

        if (accountRepository.existsByLoginId(request.loginId())) {
            throw new DuplicateResourceException("이미 사용 중인 로그인 아이디입니다.");
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        Account account = new Account(
                request.loginId(),
                encodedPassword,
                request.displayName(),
                request.role()
        );

        Account saved = accountRepository.save(account);
        return AccountResponse.from(saved);
    }

    public AccountResponse login(AccountLoginRequest request) {
        Account account = accountRepository
                .findByLoginId(request.loginId())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "아이디 또는 비밀번호가 올바르지 않습니다."
                ));
        if(!passwordEncoder.matches(
                request.password(),
                account.getPassword()
        )) {
            throw new InvalidCredentialsException(
                    "아이디 또는 비밀번호가 올바르지 않습니다."
            );
        }
        return AccountResponse.from(account);
    }

    public AccountResponse findResponseByLoginId(String loginId) {
        Account account = accountRepository
                .findByLoginId(loginId)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "아이디 또는 비밀번호가 올바르지 않습니다."
                ));

        return AccountResponse.from(account);
    }
}
