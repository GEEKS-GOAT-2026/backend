package geeks.dongnea.domain.club.dto;

import geeks.dongnea.domain.club.entity.Club;
import lombok.Getter;

@Getter
public class ClubListResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final String category;
    private final String profileImg;

    private ClubListResponse(Long id, String name, String description, String category, String profileImg) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.profileImg = profileImg;
    }

    public static ClubListResponse from(Club club) {
        return new ClubListResponse(
                club.getId(),
                club.getName(),
                club.getDescription(),
                club.getCategory(),
                club.getProfileImg()
        );
    }
}
