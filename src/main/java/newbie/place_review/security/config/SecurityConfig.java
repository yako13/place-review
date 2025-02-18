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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Profile("!dev")
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();

        http.sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                                         .maximumSessions(1)
                                         .maxSessionsPreventsLogin(true)
        );

        http.authorizeHttpRequests(request -> request.requestMatchers("/").permitAll()
                                                     .requestMatchers("/live-chat/**").permitAll()
                                                     .requestMatchers("/feedback/**").permitAll()
                                                     .requestMatchers("/place/**").permitAll()
                                                     .requestMatchers("/review/**").permitAll()
                                                     .requestMatchers("/find/**").permitAll()
                                                     .requestMatchers("/sign-in/**", "/sign-up/**").permitAll()
                                                     .requestMatchers("/api/v1/**").permitAll()
                                                     .requestMatchers("/assets/**").permitAll()
                                                     .requestMatchers("/error").permitAll()
                                                     .anyRequest().authenticated()
        );

        http.csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
                                          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                          .ignoringRequestMatchers("/sign-in/**", "/sign-up/**")
                                          .ignoringRequestMatchers("/api/v1/**")
        );

        // Basic 인증 사용 안함
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.formLogin(flc -> flc.loginPage("/sign-in")
                                 .usernameParameter("email")
                                 .passwordParameter("password")
                                 .defaultSuccessUrl("/")
                                 .failureUrl("/sign-in?error")
        );

        http.logout(logout -> logout.logoutUrl("/sign-out")
                                    .logoutSuccessUrl("/sign-in")
                                    .invalidateHttpSession(true)
                                    .clearAuthentication(true)
                                    .deleteCookies("JSESSIONID")
        );

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/")
                                         .permitAll()
        );

        http.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
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
