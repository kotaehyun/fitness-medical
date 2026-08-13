package com.fitnessmedical.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

import jakarta.servlet.http.HttpServletResponse;


/**
 * [공부/면접] Spring Security — SecurityFilterChain
 *
 * Q. SecurityFilterChain이란?
 * A. HTTP 요청이 Controller에 도달하기 전 거치는 필터들의 순서·규칙 묶음.
 *    authorizeHttpRequests로 URL·HTTP 메서드별 인증/권한을 정의한다.
 *
 * Q. permitAll() vs authenticated() vs hasRole()?
 * A. permitAll — 누구나 접근(회원가입·로그인 API).
 *    authenticated — 로그인 세션 필요(/api/auth/me, /api/members/**).
 *    hasRole("PROFESSIONAL") — ROLE_PROFESSIONAL 권한 필요(피드백 작성 POST).
 *    hasRole("ADMIN") — ROLE_ADMIN 권한 필요(전문직 승인 API).
 *
 * Q. 로그인 없이 /api/auth/me 가 403이던 이유?
 * A. Spring Security 기본은 미인증도 AccessDenied로 403을 준다.
 *    REST에서는 미인증=401, 권한 없음=403으로 나눈다.
 *
 * Q. PasswordEncoder(BCrypt)?
 * A. 평문을 DB에 저장하지 않고 one-way 해시로 저장·비교한다.
 *    matches(입력, 저장값)만으로 검증 — 역산으로 원문을 복구할 수 없다.
 *
 * Spring Security의 요청 접근 규칙을 설정합니다.
 */
@Configuration
public class SecurityConfig {

    // 회원가입: encode(평문) → BCrypt 암호문 저장
    // 로그인: matches(평문, 저장된 암호문) → 일치 여부 확인
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //    loginId
    //    → AccountRepository.findByLoginId()
    //    → Account의 BCrypt 암호문·역할을 UserDetails로 변환
    //    → MEMBER / PROFESSIONAL / ADMIN 권한 생성
    @Bean
    public  AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        // Spring Security가 UserDetailsService 등을 묶어 제공하는 인증 진입점
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API 시연 단계: CSRF 토큰 없이 POST/PUT 가능하게 함 (학습·로컬 개발용)
                // 실제 로그인 방식을 결정할 때 보안 설정을 다시 검토해야 합니다.
                .csrf(csrf -> csrf.disable())
                // WebConfig(CorsRegistry)에 등록한 CORS 정책을 Security 필터에도 적용
                .cors(Customizer.withDefaults())
                // region [핵심로직] URL별 인증·권한
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 허용 — 가입·로그인·H2 콘솔(로컬 DB 확인)
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/h2-console/**"
                        ).permitAll()
                        // 현재 로그인 사용자 조회 — 세션이 있어야 함
                        .requestMatchers("/api/auth/me").authenticated()
                        // 전문가만 회원 피드백 작성 — HttpMethod까지 지정해 GET 등은 다른 규칙 적용 가능
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/members/*/feedback"
                        ).hasRole("PROFESSIONAL")
                        // 관리자만 전문직 승인/해제. /api/** permitAll 보다 앞에 둔다
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 건강·회원 데이터는 로그인 필수. 본인 소유권은 Service에서 검증
                        .requestMatchers("/api/members/**").authenticated()
                        // AI 시연 등 나머지 /api/** 는 로컬 학습용으로 열어 둠
                        .requestMatchers("/api/**").permitAll()
                        // 위에 매칭되지 않은 경로는 인증 필요
                        .anyRequest().authenticated()
                )
                // endregion
                // H2 콘솔은 iframe을 사용하므로 같은 출처의 frame 접근을 허용합니다.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // region [하드코딩] 미인증 401 / 권한없음 403
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeAuthError(response, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.")
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeAuthError(response, HttpStatus.FORBIDDEN, "접근 권한이 없습니다.")
                        )
                );
                // endregion

        return http.build();
    }

    private static void writeAuthError(
            HttpServletResponse response,
            HttpStatus status,
            String message
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                "{\"timestamp\":\"" + LocalDateTime.now()
                        + "\",\"status\":" + status.value()
                        + ",\"message\":\"" + message + "\"}"
        );
    }


}
