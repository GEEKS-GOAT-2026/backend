package geeks.dongnea.global.security.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GoogleOAuth2AuthorizationRequestResolverTest {

    @Test
    void shouldAlwaysRequestGoogleAccountSelection() {
        OAuth2AuthorizationRequest request = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .clientId("client-id")
                .redirectUri("https://backend.example.com/login/oauth2/code/google")
                .state("state")
                .build();

        OAuth2AuthorizationRequest resolved =
                GoogleOAuth2AuthorizationRequestResolver.addAccountChooser(request);

        assertThat(resolved).isNotNull();
        assertThat(resolved.getAdditionalParameters())
                .containsEntry("prompt", "select_account");
    }
}
