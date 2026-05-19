package geeks.dongnea.domain.event.repository;

import geeks.dongnea.domain.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
            select e
            from Event e
            where e.published = true
              and (
                :keyword is null or :keyword = ''
                or lower(e.title) like lower(concat('%', :keyword, '%'))
                or lower(e.description) like lower(concat('%', :keyword, '%'))
                or lower(e.club.name) like lower(concat('%', :keyword, '%'))
              )
              and (:clubId is null or e.club.id = :clubId)
              and (:fromDate is null or e.eventDate >= :fromDate)
              and (:toDate is null or e.eventDate <= :toDate)
            """)
    Page<Event> findEvents(
            @Param("keyword") String keyword,
            @Param("clubId") Long clubId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    List<Event> findByPublishedTrueOrderByEventDateAsc(Pageable pageable);
}
