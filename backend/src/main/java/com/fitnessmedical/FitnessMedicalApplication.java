package com.fitnessmedical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 애플리케이션의 시작점입니다.
 *
 * <p>@SpringBootApplication에는 다음 세 가지 기능이 포함되어 있습니다.</p>
 * <ul>
 *     <li>@Configuration: 이 클래스가 Spring 설정 클래스임을 알립니다.</li>
 *     <li>@EnableAutoConfiguration: 의존성을 보고 필요한 설정을 자동으로 구성합니다.</li>
 *     <li>@ComponentScan: 현재 패키지 아래의 Controller, Service 등을 찾아 Bean으로 등록합니다.</li>
 * </ul>
 */
@SpringBootApplication
public class FitnessMedicalApplication {

    public static void main(String[] args) {
        // JVM에서 가장 먼저 실행되는 main 메서드입니다.
        // run()이 Spring 컨테이너를 만들고 내장 Tomcat 서버를 시작합니다.
        SpringApplication.run(FitnessMedicalApplication.class, args);
    }
}
