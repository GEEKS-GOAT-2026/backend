package geeks.dongnea.global.security.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.frontend.redirect-uri:http://localhost:3000}")
    private String frontendRedirectUri;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        // 내부 예외 메시지는 민감할 수 있으므로 사용자 친화적인 메시지로 변환
        String userMessage = "로그인에 실패했습니다. 학교 계정(@inha.edu 또는 @inha.ac.kr)으로 다시 시도하세요.";
        String reason = exception instanceof org.springframework.security.oauth2.core.OAuth2AuthenticationException oauthException
                ? oauthException.getError().getErrorCode()
                : "oauth_login_failed";

        log.warn("OAuth login failed. reason={}, message={}", reason, exception.getMessage());

        SecurityContextHolder.clearContext();
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");

        String targetUrl = UriComponentsBuilder
                .fromUriString(frontendRedirectUri)
                .queryParam("error", "oauth_login_failed")
                .queryParam("reason", reason)
                .queryParam("message", userMessage)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
