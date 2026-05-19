package geeks.dongnea.global.security.controller;

import geeks.dongnea.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/local")
@Profile("local")
@RequiredArgsConstructor
public class LocalAuthController {

    private final JwtUtil jwtUtil;

    @GetMapping("/auth-token")
    public ResponseEntity<Map<String, String>> createLocalToken(
            @RequestParam(defaultValue = "local-test@inha.edu") String email
    ) {
        return ResponseEntity.ok(Map.of(
                "accessToken", jwtUtil.createToken(email, "ROLE_USER")
        ));
    }
}
