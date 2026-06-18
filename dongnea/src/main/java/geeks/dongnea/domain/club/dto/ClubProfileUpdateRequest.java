package geeks.dongnea.domain.club.dto;

import lombok.Getter;

@Getter
public class ClubProfileUpdateRequest {
    private String description;
    private String activityDescription;
    private String category;
    private String profileImg;
    private String contact;
    private String instagramUrl;
}
