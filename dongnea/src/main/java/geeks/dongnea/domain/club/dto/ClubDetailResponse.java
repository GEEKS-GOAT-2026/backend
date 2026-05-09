package geeks.dongnea.domain.club.dto;

import geeks.dongnea.domain.club.entity.Club;
import lombok.Getter;

import java.util.List;

@Getter
public class ClubDetailResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final String activityDescription;
    private final String category;
    private final String profileImg;
    private final String contact;
    private final String instagramUrl;
    private final List<RecruitmentSummaryResponse> recruitments;

    private ClubDetailResponse(
            Long id,
            String name,
            String description,
            String activityDescription,
            String category,
            String profileImg,
            String contact,
            String instagramUrl,
            List<RecruitmentSummaryResponse> recruitments
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.activityDescription = activityDescription;
        this.category = category;
        this.profileImg = profileImg;
        this.contact = contact;
        this.instagramUrl = instagramUrl;
        this.recruitments = recruitments;
    }

    public static ClubDetailResponse of(Club club, List<RecruitmentSummaryResponse> recruitments) {
        return new ClubDetailResponse(
                club.getId(),
                club.getName(),
                club.getDescription(),
                club.getActivityDescription(),
                club.getCategory(),
                club.getProfileImg(),
                club.getContact(),
                club.getInstagramUrl(),
                recruitments
        );
    }
}
