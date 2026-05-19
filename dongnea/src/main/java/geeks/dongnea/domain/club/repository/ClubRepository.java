package geeks.dongnea.domain.club.repository;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.Recruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ClubRepository extends JpaRepository<Club, Long> {
    // 이름으로 동아리 검색 (필요 시)
    boolean existsByName(String name);

    @Query("""
            select c
            from Club c
            where (:category is null or :category = '' or lower(c.category) = lower(:category))
              and (
                :keyword is null or :keyword = ''
                or lower(c.name) like lower(concat('%', :keyword, '%'))
                or lower(c.description) like lower(concat('%', :keyword, '%'))
                or lower(c.activityDescription) like lower(concat('%', :keyword, '%'))
              )
              and (
                :hasActiveRecruitment is null
                or (
                  :hasActiveRecruitment = true
                  and exists (
                    select 1
                    from Recruitment r
                    where r.club = c
                      and r.isActive = true
                      and (
                        r.isAlwaysOpen = true
                        or (
                          (r.startDate is null or r.startDate <= :today)
                          and (r.endDate is null or r.endDate >= :today)
                        )
                      )
                  )
                )
                or (
                  :hasActiveRecruitment = false
                  and not exists (
                    select 1
                    from Recruitment r
                    where r.club = c
                      and r.isActive = true
                      and (
                        r.isAlwaysOpen = true
                        or (
                          (r.startDate is null or r.startDate <= :today)
                          and (r.endDate is null or r.endDate >= :today)
                        )
                      )
                  )
                )
              )
            """)
    Page<Club> findClubsForList(
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("hasActiveRecruitment") Boolean hasActiveRecruitment,
            @Param("today") LocalDate today,
            Pageable pageable
    );
}
