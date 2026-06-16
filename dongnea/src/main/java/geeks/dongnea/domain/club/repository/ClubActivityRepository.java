package geeks.dongnea.domain.club.repository;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.ClubActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubActivityRepository extends JpaRepository<ClubActivity, Long> {

    List<ClubActivity> findByClubOrderByStartDateDescIdDesc(Club club);

    long countByClub(Club club);
}
