package com.fitnessmedical.controller;


import com.fitnessmedical.dto.account.AccountCreateRequest;
import com.fitnessmedical.dto.account.AccountResponse;
import com.fitnessmedical.dto.account.AccountLoginRequest;
import com.fitnessmedical.service.AccountService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthController(
            AccountService accountService,
            AuthenticationManager authenticationManager
    ) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse signup(
            @Valid @RequestBody AccountCreateRequest request
    ) {
        return accountService.create(request);
    }

    @PostMapping("/login")
    public AccountResponse login(
            @Valid @RequestBody AccountLoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.loginId(),
                                request.password()
                        )
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        return accountService.findResponseByLoginId(request.loginId());
    }

    @GetMapping("/me")
    public AccountResponse me(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return accountService.findResponseByLoginId(
                userDetails.getUsername()
        );
    }

}