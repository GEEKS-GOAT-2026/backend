package geeks.dongnea.domain.event.service;

import geeks.dongnea.domain.event.dto.EventListResponse;
import geeks.dongnea.domain.event.dto.EventPageResponse;
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

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}
