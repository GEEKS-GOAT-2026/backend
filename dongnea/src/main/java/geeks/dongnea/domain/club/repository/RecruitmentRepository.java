package geeks.dongnea.domain.club.repository;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
    // 특정 동아리의 모든 공고 조회
    List<Recruitment> findByClub(Club club);

    // 현재 활성화된 공고만 조회 (isActive == true)
    List<Recruitment> findByIsActiveTrue();

    List<Recruitment> findByClubAndIsActiveTrue(Club club);

    boolean existsByClubAndIsActiveTrue(Club club);

    @Query("""
            select r
            from Recruitment r
            where r.club = :club
              and r.isActive = true
              and (
                r.isAlwaysOpen = true
                or (
                  (r.startDate is null or r.startDate <= :today)
                  and (r.endDate is null or r.endDate >= :today)
                )
              )
            order by r.isAlwaysOpen desc, r.endDate asc
            """)
    List<Recruitment> findOpenRecruitmentsByClub(
            @Param("club") Club club,
            @Param("today") LocalDate today
    );

    @Query("""
            select count(r) > 0
            from Recruitment r
            where r.club = :club
              and r.isActive = true
              and (
                r.isAlwaysOpen = true
                or (
                  (r.startDate is null or r.startDate <= :today)
                  and (r.endDate is null or r.endDate >= :today)
                )
              )
            """)
    boolean existsOpenRecruitmentByClub(
            @Param("club") Club club,
            @Param("today") LocalDate today
    );
}
