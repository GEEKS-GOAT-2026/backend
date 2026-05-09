package geeks.dongnea.global.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component // 스프링에게 "이거 내가 쓸 공장 기계니까 네가 관리해!" 라고 알려줍니다.
public class JwtUtil {

    private final SecretKey secretKey;

    // 토큰 유효시간 설정 (예: 1시간 = 1000ms * 60초 * 60분)
    private final long expTime = 1000L * 60 * 60;

    // application-secret.yml에 적어둔 엄청 긴 비밀문자열(마스터키)을 잉크로 불러옵니다!
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 1. 토큰 발급 기계
     * 유저가 로그인에 성공하면, 이메일과 권한을 적어서 토큰을 만들어줍니다.
     */
    public String createToken(String email, String role) {
        return Jwts.builder()
                .claim("email", email) // 토큰에 이메일 적기
                .claim("role", role)   // 토큰에 권한 적기 (일반 유저)
                .issuedAt(new Date(System.currentTimeMillis())) // 발급 시간
                .expiration(new Date(System.currentTimeMillis() + expTime)) // 만료 시간 (1시간 뒤면 사라짐)
                .signWith(secretKey) // ⭐️마스터키로 위조 방지
                .compact(); // 토큰 완성
    }

    /**
     * 2. 토큰 검사기
     * 프론트엔드가 API를 요청할 때 가져온 토큰이 진짜인지(위조/만료 안됐는지) 검사합니다.
     */
    public boolean validateToken(String token) {
        try {
            // 우리 도장(secretKey)을 가져와서 이 토큰이 진짜인지 뜯어봅니다.
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true; // 위조되지 않았고, 시간도 안 지났음! 통과!
        } catch (Exception e) {
            return false; // 위조되었거나 시간이 지났음! 쫓아냄!
        }
    }

    /**
     * 3. 이메일(정보) 읽어내기
     * 검사를 통과한 진짜 이메일을 읽어서 누군지 확인합니다.
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    public String getRoleFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }
}
