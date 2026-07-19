package com.fitnessmedical.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/** Spring Security의 요청 접근 규칙을 설정합니다. */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 현재는 REST API 시연 단계이므로 CSRF 검사를 임시로 비활성화합니다.
                // 실제 로그인 방식을 결정할 때 보안 설정을 다시 검토해야 합니다.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 학습 단계에서는 API와 H2 콘솔에 로그인 없이 접근할 수 있습니다.
                        .requestMatchers("/api/**", "/h2-console/**").permitAll()
                        // 위 경로를 제외한 요청은 인증이 필요합니다.
                        .anyRequest().authenticated())
                // H2 콘솔은 iframe을 사용하므로 같은 출처의 frame 접근을 허용합니다.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
