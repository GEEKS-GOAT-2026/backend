package geeks.dongnea.global.security.oauth;

import geeks.dongnea.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import geeks.dongnea.domain.user.service.UserService;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        try {
            User user = userService.saveOrUpdate(email, name);
            log.info("OAuth user saved or updated. userId={}, email={}", user.getId(), user.getEmail());
        } catch (IllegalArgumentException e) {
            log.warn("OAuth user rejected. email={}, reason={}", email, e.getMessage());
            OAuth2Error error = new OAuth2Error("invalid_school_email", e.getMessage(), null);
            throw new OAuth2AuthenticationException(error, e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("OAuth user save failed. email={}", email, e);
            OAuth2Error error = new OAuth2Error("user_save_failed", "사용자 저장에 실패했습니다.", null);
            throw new OAuth2AuthenticationException(error, "사용자 저장에 실패했습니다.", e);
        }

        return new DefaultOAuth2User(Collections.emptyList(), attributes, "email");
    }
}
