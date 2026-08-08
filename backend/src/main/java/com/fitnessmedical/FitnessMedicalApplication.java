package com.fitnessmedical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [공부/면접] Spring Boot 진입점 (@SpringBootApplication)
 *
 * Q. @SpringBootApplication은 무엇을 합친 어노테이션?
 * A. @Configuration + @EnableAutoConfiguration + @ComponentScan(현재 패키지 com.fitnessmedical 하위).
 *    설정 클래스 선언, 자동 구성, Bean 스캔을 한 줄로 묶는다.
 *
 * Q. @EnableAutoConfiguration 동작?
 * A. classpath의 starter( spring-boot-starter-web, data-jpa 등 )를 보고
 *    DataSource, DispatcherServlet, Hibernate 등을 조건(@Conditional)에 맞게 자동 등록한다.
 *
 * Q. ComponentScan 범위?
 * A. 이 클래스가 있는 패키지(com.fitnessmedical)와 하위의 @Component, @Service, @Repository,
 *    @Controller, @Configuration 등이 Bean으로 등록된다. 형제·상위 패키지는 스캔하지 않는다.
 *
 * Q. SpringApplication.run()이 하는 일?
 * A. ApplicationContext 생성 → auto-config·Bean 등록 → 내장 Tomcat 기동 → CommandLineRunner 실행.
 *
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
        // JVM 진입점 — run()에 primary source(FitnessMedicalApplication.class)를 넘겨
        // 어떤 @Configuration·ComponentScan 루트로 부트스트랩할지 Spring에 알린다
        SpringApplication.run(FitnessMedicalApplication.class, args);
    }
}
