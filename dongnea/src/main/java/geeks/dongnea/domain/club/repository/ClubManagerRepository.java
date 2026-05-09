package geeks.dongnea.domain.club.repository;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.ClubManager;
import geeks.dongnea.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubManagerRepository extends JpaRepository<ClubManager, Long> {
    // 특정 유저가 관리 중인 모든 동아리 조회
    List<ClubManager> findByUser(User user);

    // 특정 유저가 특정 동아리의 관리자인지 확인
    Optional<ClubManager> findByUserAndClub(User user, Club club);
}