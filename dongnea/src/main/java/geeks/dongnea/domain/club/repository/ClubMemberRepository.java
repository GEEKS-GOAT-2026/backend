package geeks.dongnea.domain.club.repository;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {

    long countByClub(Club club);

    @Query("""
            select m
            from ClubMember m
            where m.club = :club
              and (:status is null or :status = '' or m.status = :status)
              and (
                :keyword is null or :keyword = ''
                or lower(m.name) like lower(concat('%', :keyword, '%'))
                or lower(m.email) like lower(concat('%', :keyword, '%'))
                or lower(m.major) like lower(concat('%', :keyword, '%'))
              )
            order by m.name asc
            """)
    List<ClubMember> findMembers(
            @Param("club") Club club,
            @Param("status") String status,
            @Param("keyword") String keyword
    );
}
