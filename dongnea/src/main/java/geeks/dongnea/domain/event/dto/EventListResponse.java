package geeks.dongnea.domain.event.dto;

import geeks.dongnea.domain.event.entity.Event;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EventListResponse {

    private final Long id;
    private final Long clubId;
    private final String clubName;
    private final String title;
    private final String description;
    private final LocalDate eventDate;
    private final String location;
    private final String imageUrl;

    private EventListResponse(
            Long id,
            Long clubId,
            String clubName,
            String title,
            String description,
            LocalDate eventDate,
            String location,
            String imageUrl
    ) {
        this.id = id;
        this.clubId = clubId;
        this.clubName = clubName;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    public static EventListResponse from(Event event) {
        return new EventListResponse(
                event.getId(),
                event.getClub().getId(),
                event.getClub().getName(),
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getLocation(),
                event.getImageUrl()
        );
    }
}
