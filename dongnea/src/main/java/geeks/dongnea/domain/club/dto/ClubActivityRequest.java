package geeks.dongnea.domain.club.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ClubActivityRequest {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String imageUrl;
}
