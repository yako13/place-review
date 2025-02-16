package newbie.place_review.security.config;

import newbie.place_review.security.filter.CsrfCookieFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Profile("dev")
@Configuration
@EnableWebSecurity(debug = true) // 스프링 시큐리티 디버그 모드 켜기
public class SecurityDevConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 헤더나 파라미터에 있는 Csrf 토큰의 값을 다루는 핸들러
        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

        // 세션관리 설정
        http.sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // 항상 세션 생성
                                         .maximumSessions(1) // 한 계정 당 최대 세션 1개로 제한
                                         .maxSessionsPreventsLogin(true) // 최대 세션에 도달할 경우 로그인을 막음
        );

        // 경로 접근 제한 설정
        http.authorizeHttpRequests(request -> request.requestMatchers(PathRequest.toH2Console()).permitAll() // H2 콘솔 요청 허용
                                                     .anyRequest().permitAll() // 모든 요청 허용
        );

        // H2 Frame 설정
        http.headers(hc -> hc.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        // CSRF 설정
        http.csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler) // Csrf토큰을 다루는 핸들러 등록
                                          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // HttpOnly를 false로 설정, 이러면 스크립트로 쿠키 제어 가능
                                          .ignoringRequestMatchers("/sign-in/**", "/sign-up/**")
                                          .ignoringRequestMatchers("/api/v1/**")
                                          .ignoringRequestMatchers("/h2-console/**") // H2 Console은 제외
        );

        // HttpBasic 인증 설정
        http.httpBasic(Customizer.withDefaults()); // 기본 값

        // FormLogin 설정
        http.formLogin(flc -> flc.loginPage("/sign-in") // 로그인할 페이지 경로, 없으면 기본 폼 로그인 페이지 생성
                                 .usernameParameter("email")
                                 .passwordParameter("password")
                                 .defaultSuccessUrl("/") // 로그인에 성공하면 이동 할 경로
                                 .failureUrl("/sign-in?error") // 로그인에 실패하면 이동 할 경로
        );

        // Logout 설정
        http.logout(logout -> logout.logoutUrl("/sign-out") // 로그아웃 요청 경로
                                    .logoutSuccessUrl("/sign-in") // 로그아웃 성공하면 이동 할 경로
                                    .invalidateHttpSession(true) // 로그아웃 시 세션 만료 시킴
                                    .clearAuthentication(true) // 로그아웃 시 인증 제거
                                    .deleteCookies("JSESSIONID") // 로그아웃 시 쿠키 제거
        );

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/")
                                         .permitAll()
        );

        http.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class); // CSRF 쿠키 필터를 HttpBasic 인증 앞에 배치

        return http.build(); // 설정 생성
    }

    /**
     * 패스워드 인코더 설정, 빈으로 등록된 PasswordEncoder를 사용해서 비밀번호를 확인함
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // DelegatingPasswordEncoder를 생성함. 얘는 알아서 암호화 방식에 맞게 비교함.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 동시 세션 제어, 로그아웃 시 SessionInformation 정보도 삭제하도록 하기
     *
     * @see <a href="https://www.inflearn.com/community/questions/40072/동시-세션-제어-동일-브라우저에서-로그아웃이-정책-미적용">참고</a>
     */
    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }
}
