package geeks.dongnea.global.security.oauth;

import geeks.dongnea.global.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class OAuth2SuccessHandlerTest {

    @Test
    void shouldRedirectNonSchoolAccountThroughFailureHandler() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        OAuth2FailureHandler failureHandler = new OAuth2FailureHandler();
        ReflectionTestUtils.setField(
                failureHandler,
                "frontendRedirectUri",
                "https://frontend.example.com/login"
        );

        OAuth2SuccessHandler successHandler = new OAuth2SuccessHandler(jwtUtil, failureHandler);
        ReflectionTestUtils.setField(
                successHandler,
                "frontendRedirectUri",
                "https://frontend.example.com/login"
        );

        DefaultOAuth2User principal = new DefaultOAuth2User(
                List.of(),
                Map.of("email", "personal@gmail.com", "name", "Personal User"),
                "email"
        );
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(principal, null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        successHandler.onAuthenticationSuccess(request, response, authentication);

        assertThat(response.getStatus()).isEqualTo(302);
        assertThat(response.getRedirectedUrl())
                .contains("https://frontend.example.com/login?error=oauth_login_failed")
                .contains("reason=invalid_school_email");
        verifyNoInteractions(jwtUtil);
    }
}
