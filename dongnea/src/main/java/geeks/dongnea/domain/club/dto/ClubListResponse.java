package geeks.dongnea.domain.club.dto;

import geeks.dongnea.domain.club.entity.Club;
import geeks.dongnea.domain.club.entity.Recruitment;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class ClubListResponse {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final Long id;
    private final String name;
    private final String description;
    private final String category;
    private final String profileImg;
    private final boolean activeRecruitment;
    private final String recruitmentDisplayText;

    private ClubListResponse(
            Long id,
            String name,
            String description,
            String category,
            String profileImg,
            boolean activeRecruitment,
            String recruitmentDisplayText
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.profileImg = profileImg;
        this.activeRecruitment = activeRecruitment;
        this.recruitmentDisplayText = recruitmentDisplayText;
    }

    public static ClubListResponse of(Club club, Recruitment openRecruitment) {
        boolean activeRecruitment = openRecruitment != null;

        return new ClubListResponse(
                club.getId(),
                club.getName(),
                club.getDescription(),
                club.getCategory(),
                club.getProfileImg(),
                activeRecruitment,
                getRecruitmentDisplayText(openRecruitment)
        );
    }

    private static String getRecruitmentDisplayText(Recruitment recruitment) {
        if (recruitment == null) {
            return "모집마감";
        }

        if (recruitment.isAlwaysOpen()) {
            return "상시모집";
        }

        if (recruitment.getStartDate() != null && recruitment.getEndDate() != null) {
            return DATE_FORMATTER.format(recruitment.getStartDate())
                    + " ~ "
                    + DATE_FORMATTER.format(recruitment.getEndDate());
        }

        return "기간 모집";
    }
}
