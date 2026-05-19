package geeks.dongnea.domain.event.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class EventPageResponse {

    private final List<EventListResponse> content;
    private final int page;
    private final int size;
    private final boolean hasNext;

    private EventPageResponse(List<EventListResponse> content, int page, int size, boolean hasNext) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.hasNext = hasNext;
    }

    public static EventPageResponse from(Page<EventListResponse> events) {
        return new EventPageResponse(
                events.getContent(),
                events.getNumber(),
                events.getSize(),
                events.hasNext()
        );
    }
}
