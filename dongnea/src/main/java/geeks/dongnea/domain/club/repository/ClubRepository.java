package geeks.dongnea.domain.club.repository;

import geeks.dongnea.domain.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long> {
    // 이름으로 동아리 검색 (필요 시)
    boolean existsByName(String name);
}