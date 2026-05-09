package geeks.dongnea.domain.club.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class ClubPageResponse {

    private final List<ClubListResponse> content;
    private final int page;
    private final int size;
    private final boolean hasNext;

    private ClubPageResponse(List<ClubListResponse> content, int page, int size, boolean hasNext) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.hasNext = hasNext;
    }

    public static ClubPageResponse from(Page<ClubListResponse> clubs) {
        return new ClubPageResponse(
                clubs.getContent(),
                clubs.getNumber(),
                clubs.getSize(),
                clubs.hasNext()
        );
    }
}
