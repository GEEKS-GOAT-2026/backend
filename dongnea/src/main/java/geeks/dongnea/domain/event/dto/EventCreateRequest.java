package geeks.dongnea.domain.event.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EventCreateRequest {
    private String title;
    private String description;
    private LocalDate eventDate;
    private String location;
    private String imageUrl;
    private Boolean published;
}
