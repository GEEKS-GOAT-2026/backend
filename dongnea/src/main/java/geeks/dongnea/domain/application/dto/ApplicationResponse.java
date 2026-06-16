package geeks.dongnea.domain.application.dto;

import geeks.dongnea.domain.application.entity.Application;
import lombok.Getter;

import java.util.Map;

@Getter
public class ApplicationResponse {

    private final Long id;
    private final Long recruitmentId;
    private final String recruitmentTitle;
    private final Long clubId;
    private final String clubName;
    private final Long userId;
    private final String userName;
    private final String userEmail;
    private final Map<String, Object> answers;
    private final String status;

    private ApplicationResponse(
            Long id,
            Long recruitmentId,
            String recruitmentTitle,
            Long clubId,
            String clubName,
            Long userId,
            String userName,
            String userEmail,
            Map<String, Object> answers,
            String status
    ) {
        this.id = id;
        this.recruitmentId = recruitmentId;
        this.recruitmentTitle = recruitmentTitle;
        this.clubId = clubId;
        this.clubName = clubName;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.answers = answers;
        this.status = status;
    }

    public static ApplicationResponse from(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getRecruitment().getId(),
                application.getRecruitment().getTitle(),
                application.getRecruitment().getClub().getId(),
                application.getRecruitment().getClub().getName(),
                application.getUser().getId(),
                application.getUser().getName(),
                application.getUser().getEmail(),
                application.getAnswers(),
                application.getStatus()
        );
    }
}
