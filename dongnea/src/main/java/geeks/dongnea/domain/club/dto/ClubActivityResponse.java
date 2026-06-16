package geeks.dongnea.domain.club.dto;

import geeks.dongnea.domain.club.entity.ClubActivity;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ClubActivityResponse {

    private final Long id;
    private final Long clubId;
    private final String clubName;
    private final String title;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String imageUrl;

    private ClubActivityResponse(
            Long id,
            Long clubId,
            String clubName,
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String imageUrl
    ) {
        this.id = id;
        this.clubId = clubId;
        this.clubName = clubName;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
    }

    public static ClubActivityResponse from(ClubActivity activity) {
        return new ClubActivityResponse(
                activity.getId(),
                activity.getClub().getId(),
                activity.getClub().getName(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getStartDate(),
                activity.getEndDate(),
                activity.getImageUrl()
        );
    }
}
