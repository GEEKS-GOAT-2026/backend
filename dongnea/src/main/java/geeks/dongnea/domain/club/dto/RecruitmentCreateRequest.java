package geeks.dongnea.domain.club.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
public class RecruitmentCreateRequest {
    private String title;
    private String summary;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean alwaysOpen;
    private Map<String, Object> formSchema;
}
