package geeks.dongnea.domain.club.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ClubNoticeRequest {
    private String title;
    private String content;
    private LocalDate noticeDate;
    private String badge;
    private Boolean pinned;
}
