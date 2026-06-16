package geeks.dongnea.domain.user.service;

import geeks.dongnea.domain.club.dto.ManagedClubResponse;
import geeks.dongnea.domain.club.repository.ClubManagerRepository;
import geeks.dongnea.domain.user.dto.UserResponse;
import geeks.dongnea.domain.user.entity.User;
import geeks.dongnea.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ClubManagerRepository clubManagerRepository;

    @Transactional
    public User saveOrUpdate(String email, String name) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();

        // 1. 학교 계정 검증 (@inha.edu 또는 @inha.ac.kr)
        if (normalizedEmail == null || !(normalizedEmail.endsWith("@inha.edu") || normalizedEmail.endsWith("@inha.ac.kr"))) {
            throw new IllegalArgumentException("인하대학교 계정만 이용 가능합니다.");
        }

        // 2. 이름 파싱 (박현진/학생/컴퓨터공학과 -> 박현진)
        String cleanName = (name != null && name.contains("/"))
                ? name.split("/")[0] : name;
        if (cleanName == null || cleanName.isBlank()) {
            cleanName = normalizedEmail.substring(0, normalizedEmail.indexOf("@"));
        }
        String displayName = cleanName;

        // 3. Upsert 로직 (있으면 업데이트, 없으면 신규 가입)
        return userRepository.findByEmail(normalizedEmail)
                .map(user -> user.update(displayName)) // User 엔티티에 update 메서드 필요!
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(normalizedEmail)
                        .name(displayName)
                        .build()));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserInfo(String email) {
        // 💡 DB(Repository)에서 이메일로 유저를 진짜 찾아옵니다.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 찾아온 엔티티를 응답용 DTO로 변환해서 반환합니다.
        return toResponse(user);
    }

    public UserResponse toResponse(User user) {
        var managedClubs = clubManagerRepository.findByUser(user)
                .stream()
                .map(ManagedClubResponse::from)
                .toList();

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                !managedClubs.isEmpty(),
                managedClubs
        );
    }
}
