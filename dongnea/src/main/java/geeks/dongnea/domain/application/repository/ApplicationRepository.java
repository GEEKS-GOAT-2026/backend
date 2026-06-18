package geeks.dongnea.domain.application.repository;

import geeks.dongnea.domain.application.entity.Application;
import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.Recruitment;
import geeks.dongnea.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    // 특정 유저의 지원 내역 조회
    List<Application> findByUser(User user);

    // 특정 모집 공고에 제출된 모든 지원서 조회 (회장용)
    List<Application> findByRecruitment(Recruitment recruitment);

    List<Application> findByRecruitmentClubOrderByIdDesc(Club club);

    Optional<Application> findByRecruitmentAndUser(Recruitment recruitment, User user);

    Optional<Application> findFirstByRecruitmentClubAndUserOrderByIdDesc(Club club, User user);
}
