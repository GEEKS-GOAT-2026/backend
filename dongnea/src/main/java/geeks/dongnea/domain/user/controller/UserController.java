package geeks.dongnea.domain.user.controller;

import geeks.dongnea.domain.user.dto.UserResponse;
import geeks.dongnea.domain.user.entity.User;
import geeks.dongnea.domain.user.service.UserService;
import geeks.dongnea.global.security.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 관련 API")
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    @Operation(summary = "현재 로그인한 내 정보 조회", description = "JWT 토큰을 통해 인증된 유저의 id, email, name을 반환합니다.")
    public ResponseEntity<UserResponse> getMyInfo() {
        User currentUser = currentUserService.getCurrentUser();
        UserResponse response = userService.toResponse(currentUser);
        return ResponseEntity.ok(response);
    }
}
