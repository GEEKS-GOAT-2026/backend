package geeks.dongnea.global.security.config;

import geeks.dongnea.global.security.jwt.JwtAuthenticationFilter;
import geeks.dongnea.global.security.oauth.CustomOAuth2UserService;
import geeks.dongnea.global.security.oauth.OAuth2FailureHandler;
import geeks.dongnea.global.security.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 설정을 우리가 마음대로 주무르겠다는 선언
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // 모든 도메인 허용 (개발용)
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 💡 CORS 설정 연결
                .csrf(csrf -> csrf.disable()) // 💡 REST API이므로 CSRF 비활성화

                // 1. CSRF 공격 방어 비활성화 (우리는 세션 대신 JWT 토큰을 쓸 거니까 꺼도 됩니다)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 폼 로그인, 기본 HTTP 인증 비활성화 (구글 로그인만 쓸 거니까)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 3. 세션 관리 상태를 STATELESS로 설정 (서버가 방문자를 기억하지 않음 = JWT 사용 필수)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. 출입문(URL) 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // Swagger 관련 주소는 토큰 없이 누구나 접근 가능! (통과)
                        .requestMatchers(
                                "/api-docs",
                                "/api-docs/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger",
                                "/swagger/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/api/local/**",
                                "/oauth2/**",
                                "/login/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/clubs", "/api/clubs/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/recent").permitAll()

                        // 나머지 모든 API는 무조건 로그인을 해야 접근 가능! (입구컷)
                        .anyRequest().authenticated()
                )

                // 5. 구글 로그인(OAuth2) 설정
                .oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService) // 이메일 검사기
                                )
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler(oAuth2FailureHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
