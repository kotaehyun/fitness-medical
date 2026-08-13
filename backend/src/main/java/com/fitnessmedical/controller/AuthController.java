package com.fitnessmedical.controller;


import com.fitnessmedical.dto.account.AccountCreateRequest;
import com.fitnessmedical.dto.account.AccountResponse;
import com.fitnessmedical.dto.account.AccountLoginRequest;
import com.fitnessmedical.service.AccountService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

/**
 * [공부/면접] 회원가입·로그인·현재 로그인 사용자 조회 API를 제공하는 Controller입니다.
 *
 * Q. Controller → Service → Repository 흐름은?
 * A. signup/me는 AccountService가 AccountRepository를 호출하고,
 *    login은 AuthenticationManager → AccountUserDetailsService → AccountRepository 경로로
 *    UserDetailsService + PasswordEncoder 검증 후 세션에 SecurityContext를 저장합니다.
 *
 * Q. @Valid는 어디서 동작하나요?
 * A. @RequestBody DTO(AccountCreateRequest, AccountLoginRequest)의 Bean Validation
 *    (@NotBlank 등)을 컨트롤러 진입 전에 실행합니다. 실패 시 400 Bad Request.
 *
 * Q. 세션 기반 로그인에서 SecurityContext는?
 * A. authenticate() 성공 → SecurityContext에 Authentication 저장
 *    → HttpSessionSecurityContextRepository.saveContext()로 HTTP 세션에 직렬화.
 *    이후 요청마다 세션에서 복원되어 @AuthenticationPrincipal로 principal을 주입받습니다.
 *
 * 예외( Service/GlobalExceptionHandler ): InvalidRequestException 400,
 *   DuplicateResourceException·DataIntegrityViolationException 409,
 *   InvalidCredentialsException 401
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;

    /** [공부/면접] 인증 성공 후 SecurityContext를 HTTP 세션에 저장하는 저장소 */
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public AuthController(
            AccountService accountService,
            AuthenticationManager authenticationManager
    ) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * [공부/면접] POST /api/auth/signup — 계정 생성
     *
     * 흐름: @Valid로 요청 검증 → accountService.create() → 201 Created + AccountResponse
     * 면접 포인트: 비밀번호 해싱·resolveMember 규칙은 Service 계층 책임입니다.
     */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse signup(
            @Valid @RequestBody AccountCreateRequest request
    ) {
        return accountService.create(request);
    }

    /**
     * [공부/면접] POST /api/auth/login — 세션 기반 로그인
     *
     * 흐름:
     * 1. UsernamePasswordAuthenticationToken 생성(loginId, password)
     * 2. authenticationManager.authenticate() — UserDetailsService 조회 + PasswordEncoder.matches
     * 3. SecurityContextHolder에 Authentication 설정
     * 4. securityContextRepository.saveContext() — 세션에 저장(이후 요청 인증 유지)
     * 5. accountService.findResponseByLoginId() — 클라이언트용 계정 정보 반환
     *
     * 면접 포인트: password는 authenticate() 내부에서만 사용되며 로그에 남기면 안 됩니다.
     *             실패 시 InvalidCredentialsException → 401.
     */
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

    /**
     * [공부/면접] GET /api/auth/me — 현재 세션의 로그인 사용자 정보
     *
     * @AuthenticationPrincipal UserDetails — 세션 SecurityContext에서 principal 추출
     * userDetails.getUsername()은 loginId와 동일(AccountUserDetailsService에서 설정)
     */
    @GetMapping("/me")
    public AccountResponse me(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return accountService.findResponseByLoginId(
                userDetails.getUsername()
        );
    }

    /**
     * [공부/면접] POST /api/auth/logout — 세션 무효화
     *
     * SecurityContext와 HTTP 세션을 지운다. 이후 /auth/me는 401이다.
     */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest httpRequest) {
        // region [핵심로직] SecurityContext + HTTP 세션 무효화
        SecurityContextHolder.clearContext();
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // endregion
    }

}
