package geeks.dongnea.domain.user.service;

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

    @Transactional
    public User saveOrUpdate(String email, String name) {
        // 1. 학교 계정 검증 (@inha.edu 또는 @inha.ac.kr)
        if (email == null || !(email.endsWith("@inha.edu") || email.endsWith("@inha.ac.kr"))) {
            throw new IllegalArgumentException("인하대학교 계정만 이용 가능합니다.");
        }

        // 2. 이름 파싱 (박현진/학생/컴퓨터공학과 -> 박현진)
        String cleanName = (name != null && name.contains("/"))
                ? name.split("/")[0] : name;

        // 3. Upsert 로직 (있으면 업데이트, 없으면 신규 가입)
        return userRepository.findByEmail(email)
                .map(user -> user.update(cleanName)) // User 엔티티에 update 메서드 필요!
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .name(cleanName)
                        .build()));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserInfo(String email) {
        // 💡 DB(Repository)에서 이메일로 유저를 진짜 찾아옵니다.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 찾아온 엔티티를 응답용 DTO로 변환해서 반환합니다.
        return new UserResponse(user.getId(), user.getEmail(), user.getName());
    }
}
