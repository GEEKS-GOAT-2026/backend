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

    @Query(
            value = """
                    select c.*
                    from clubs c
                    where (:category is null or :category = '' or lower(c.category) = lower(:category))
                      and (
                        :keyword is null or :keyword = ''
                        or lower(c.name) like lower(concat('%', :keyword, '%'))
                        or lower(c.description) like lower(concat('%', :keyword, '%'))
                        or lower(c.activity_description) like lower(concat('%', :keyword, '%'))
                      )
                      and (
                        :hasActiveRecruitment is null
                        or (
                          :hasActiveRecruitment = true
                          and exists (
                            select 1
                            from recruitments r
                            where r.club_id = c.id
                              and r.is_active = true
                              and (
                                r.is_always_open = true
                                or (
                                  (r.start_date is null or r.start_date <= :today)
                                  and (r.end_date is null or r.end_date >= :today)
                                )
                              )
                          )
                        )
                        or (
                          :hasActiveRecruitment = false
                          and not exists (
                            select 1
                            from recruitments r
                            where r.club_id = c.id
                              and r.is_active = true
                              and (
                                r.is_always_open = true
                                or (
                                  (r.start_date is null or r.start_date <= :today)
                                  and (r.end_date is null or r.end_date >= :today)
                                )
                              )
                          )
                        )
                      )
                    """,
            countQuery = """
                    select count(*)
                    from clubs c
                    where (:category is null or :category = '' or lower(c.category) = lower(:category))
                      and (
                        :keyword is null or :keyword = ''
                        or lower(c.name) like lower(concat('%', :keyword, '%'))
                        or lower(c.description) like lower(concat('%', :keyword, '%'))
                        or lower(c.activity_description) like lower(concat('%', :keyword, '%'))
                      )
                      and (
                        :hasActiveRecruitment is null
                        or (
                          :hasActiveRecruitment = true
                          and exists (
                            select 1
                            from recruitments r
                            where r.club_id = c.id
                              and r.is_active = true
                              and (
                                r.is_always_open = true
                                or (
                                  (r.start_date is null or r.start_date <= :today)
                                  and (r.end_date is null or r.end_date >= :today)
                                )
                              )
                          )
                        )
                        or (
                          :hasActiveRecruitment = false
                          and not exists (
                            select 1
                            from recruitments r
                            where r.club_id = c.id
                              and r.is_active = true
                              and (
                                r.is_always_open = true
                                or (
                                  (r.start_date is null or r.start_date <= :today)
                                  and (r.end_date is null or r.end_date >= :today)
                                )
                              )
                          )
                        )
                      )
                    """,
            nativeQuery = true
    )
    Page<Club> findClubsForList(
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("hasActiveRecruitment") Boolean hasActiveRecruitment,
            @Param("today") LocalDate today,
            Pageable pageable
    );
}
