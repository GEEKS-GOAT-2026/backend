package geeks.dongnea.global.security.oauth;

import geeks.dongnea.global.security.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    // 프론트엔드 리다이렉트 URI (개발 기본값은 localhost:3000). 운영 환경에서는 application-secret.yml 또는
    // 환경변수로 덮어쓰세요. 개발 중에는 쿼리 파라미터로 토큰을 전달합니다.
    @Value("${app.frontend.redirect-uri:http://localhost:3000}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. 구글 로그인에 성공한 유저 정보 꺼내기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 2. JwtUtil을 사용해 토큰 발급하기 (모든 유저는 기본적으로 ROLE_USER 부여)
        String token = jwtUtil.createToken(email, "ROLE_USER");

        // 3. 프론트엔드 주소에 토큰을 쿼리 파라미터로 붙여서 리다이렉트 처리
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.name());
        String separator = frontendRedirectUri.contains("?") ? "&" : "?";
        String targetUrl = frontendRedirectUri + separator + "token=" + encodedToken;

        log.info("OAuth login succeeded. email={}, redirectUri={}", email, frontendRedirectUri);

        // 4. 프론트엔드로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
