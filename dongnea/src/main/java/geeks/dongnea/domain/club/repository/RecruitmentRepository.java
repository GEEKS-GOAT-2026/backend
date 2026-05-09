package geeks.dongnea.domain.club.repository;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
    // 특정 동아리의 모든 공고 조회
    List<Recruitment> findByClub(Club club);

    // 현재 활성화된 공고만 조회 (isActive == true)
    List<Recruitment> findByIsActiveTrue();

    List<Recruitment> findByClubAndIsActiveTrue(Club club);
}
