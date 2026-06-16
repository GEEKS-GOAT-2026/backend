package geeks.dongnea.domain.event.service;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.service.ClubAuthorizationService;
import geeks.dongnea.domain.event.dto.EventCreateRequest;
import geeks.dongnea.domain.event.dto.EventListResponse;
import geeks.dongnea.domain.event.dto.EventPageResponse;
import geeks.dongnea.domain.event.dto.EventUpdateRequest;
import geeks.dongnea.domain.event.entity.Event;
import geeks.dongnea.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final ClubAuthorizationService clubAuthorizationService;

    public EventPageResponse getEvents(Pageable pageable, String keyword, Long clubId, LocalDate fromDate, LocalDate toDate) {
        Page<EventListResponse> events = eventRepository.findEvents(
                        normalize(keyword),
                        clubId,
                        fromDate,
                        toDate,
                        pageable)
                .map(EventListResponse::from);

        return EventPageResponse.from(events);
    }

    public List<EventListResponse> getRecentEvents(int size) {
        return eventRepository.findByPublishedTrueOrderByEventDateAsc(PageRequest.of(0, size))
                .stream()
                .map(EventListResponse::from)
                .toList();
    }

    @Transactional
    public EventListResponse createEvent(Long clubId, EventCreateRequest request) {
        Club club = clubAuthorizationService.requireManagedClub(clubId);
        validateEventRequest(request.getTitle(), request.getEventDate());

        Event event = Event.builder()
                .club(club)
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .location(request.getLocation())
                .imageUrl(request.getImageUrl())
                .published(request.getPublished())
                .build();

        return EventListResponse.from(eventRepository.save(event));
    }

    @Transactional
    public EventListResponse updateEvent(Long eventId, EventUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 행사가 없습니다."));
        clubAuthorizationService.requireManagedClub(event.getClub().getId());

        event.update(
                request.getTitle(),
                request.getDescription(),
                request.getEventDate(),
                request.getLocation(),
                request.getImageUrl(),
                request.getPublished()
        );

        return EventListResponse.from(event);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("해당 행사가 없습니다."));
        clubAuthorizationService.requireManagedClub(event.getClub().getId());
        eventRepository.delete(event);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private void validateEventRequest(String title, LocalDate eventDate) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("행사 제목은 필수입니다.");
        }

        if (eventDate == null) {
            throw new IllegalArgumentException("행사 날짜는 필수입니다.");
        }
    }
}
