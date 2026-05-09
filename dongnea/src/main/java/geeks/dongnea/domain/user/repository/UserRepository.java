package geeks.dongnea.domain.user.repository;

import geeks.dongnea.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

 public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 유저를 찾는 메서드 추가
    Optional<User> findByEmail(String email);
}