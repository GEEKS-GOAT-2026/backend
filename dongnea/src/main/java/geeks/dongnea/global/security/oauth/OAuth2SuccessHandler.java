package geeks.dongnea.global.security.oauth;

import geeks.dongnea.global.security.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
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
    private final OAuth2FailureHandler oAuth2FailureHandler;

    // 프론트엔드 리다이렉트 URI (개발 기본값은 localhost:3000). 운영 환경에서는 application-secret.yml 또는
    // 환경변수로 덮어쓰세요. 개발 중에는 쿼리 파라미터로 토큰을 전달합니다.
    @Value("${app.frontend.redirect-uri:http://localhost:3000}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = normalizeEmail(oAuth2User.getAttribute("email"));

            if (!isSchoolEmail(email)) {
                throw new IllegalArgumentException("인하대학교 계정만 이용 가능합니다.");
            }

            String token = jwtUtil.createToken(email, "ROLE_USER");
            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.name());
            String separator = frontendRedirectUri.contains("?") ? "&" : "?";
            String targetUrl = frontendRedirectUri + separator + "token=" + encodedToken;

            response.setHeader("Cache-Control", "no-store");
            log.info("OAuth login succeeded. email={}, redirectUri={}", email, frontendRedirectUri);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (IllegalArgumentException exception) {
            redirectFailure(request, response, "invalid_school_email", exception);
        } catch (RuntimeException exception) {
            log.error("OAuth success processing failed.", exception);
            redirectFailure(request, response, "oauth_processing_failed", exception);
        }
    }

    private void redirectFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            String errorCode,
            RuntimeException cause
    ) throws IOException, ServletException {
        OAuth2Error error = new OAuth2Error(errorCode);
        OAuth2AuthenticationException authenticationException = new OAuth2AuthenticationException(
                error,
                cause.getMessage(),
                cause
        );
        oAuth2FailureHandler.onAuthenticationFailure(request, response, authenticationException);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private boolean isSchoolEmail(String email) {
        return email != null && (email.endsWith("@inha.edu") || email.endsWith("@inha.ac.kr"));
    }
}
