package geeks.dongnea.global.security.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2FailureHandlerTest {

    @Test
    void shouldRedirectSchoolEmailFailureToFrontendLoginPage() throws Exception {
        OAuth2FailureHandler handler = new OAuth2FailureHandler();
        ReflectionTestUtils.setField(
                handler,
                "frontendRedirectUri",
                "https://frontend.example.com/login"
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = (MockHttpSession) request.getSession();
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2AuthenticationException exception = new OAuth2AuthenticationException(
                new OAuth2Error("invalid_school_email"),
                "인하대학교 계정만 이용 가능합니다."
        );

        handler.onAuthenticationFailure(request, response, exception);

        String redirectedUrl = response.getRedirectedUrl();
        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(session.isInvalid()).isTrue();
        assertThat(response.getHeader("Cache-Control")).contains("no-store");
        assertThat(redirectedUrl).isNotNull();
        assertThat(URI.create(redirectedUrl).getHost()).isEqualTo("frontend.example.com");
        assertThat(redirectedUrl)
                .contains("/login?error=oauth_login_failed")
                .contains("reason=invalid_school_email")
                .contains("message=")
                .doesNotContain(" ");
    }
}
