package geeks.dongnea.domain.user.controller;

import geeks.dongnea.domain.user.dto.UserResponse;
import geeks.dongnea.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "현재 로그인한 내 정보 조회", description = "JWT 토큰을 통해 인증된 유저 정보를 반환합니다.")
    public ResponseEntity<UserResponse> getMyInfo(Authentication authentication) {
        String email = extractEmail(authentication);
        UserResponse response = userService.getUserInfo(email);
        return ResponseEntity.ok(response);
    }

    private String extractEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            return oAuth2User.getAttribute("email");
        }

        return principal.toString();
    }
}
