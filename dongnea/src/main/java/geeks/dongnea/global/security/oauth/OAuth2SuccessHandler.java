package geeks.dongnea.global.security.oauth;

import geeks.dongnea.global.security.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. 구글 로그인에 성공한 유저 정보 꺼내기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 2. JwtUtil을 사용해 토큰 발급하기 (모든 유저는 기본적으로 ROLE_USER 부여)
        String token = jwtUtil.createToken(email, "ROLE_USER");

        // 3. 프론트엔드(Next.js) 주소에 토큰을 묻혀서 리다이렉트 주소 만들기
        // 나중에 실제 배포할 때는 "https://dongne.vercel.app/..." 로 바꾸면 됩니다.
        String targetUrl = "http://localhost:3000/oauth2/redirect?token=" + token;

        // 4. 프론트엔드로 슝! 날려보내기
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}