package geeks.dongnea.domain.club.repository;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.ClubNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubNoticeRepository extends JpaRepository<ClubNotice, Long> {

    List<ClubNotice> findByClubOrderByPinnedDescNoticeDateDescIdDesc(Club club);

    long countByClub(Club club);
}
