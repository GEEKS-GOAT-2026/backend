package geeks.dongnea.domain.club.dto;

import geeks.dongnea.domain.club.entity.Recruitment;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RecruitmentSummaryResponse {

    private final Long id;
    private final String title;
    private final String summary;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final boolean alwaysOpen;
    private final boolean active;

    private RecruitmentSummaryResponse(
            Long id,
            String title,
            String summary,
            LocalDate startDate,
            LocalDate endDate,
            boolean alwaysOpen,
            boolean active
    ) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alwaysOpen = alwaysOpen;
        this.active = active;
    }

    public static RecruitmentSummaryResponse from(Recruitment recruitment, LocalDate today) {
        return new RecruitmentSummaryResponse(
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getSummary(),
                recruitment.getStartDate(),
                recruitment.getEndDate(),
                recruitment.isAlwaysOpen(),
                recruitment.isOpenOn(today)
        );
    }
}
