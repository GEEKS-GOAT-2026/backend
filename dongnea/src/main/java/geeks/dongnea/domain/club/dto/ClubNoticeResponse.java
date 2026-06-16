package geeks.dongnea.domain.club.dto;

import geeks.dongnea.domain.club.entity.ClubNotice;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ClubNoticeResponse {

    private final Long id;
    private final Long clubId;
    private final String clubName;
    private final String title;
    private final String content;
    private final LocalDate noticeDate;
    private final String badge;
    private final boolean pinned;

    private ClubNoticeResponse(
            Long id,
            Long clubId,
            String clubName,
            String title,
            String content,
            LocalDate noticeDate,
            String badge,
            boolean pinned
    ) {
        this.id = id;
        this.clubId = clubId;
        this.clubName = clubName;
        this.title = title;
        this.content = content;
        this.noticeDate = noticeDate;
        this.badge = badge;
        this.pinned = pinned;
    }

    public static ClubNoticeResponse from(ClubNotice notice) {
        return new ClubNoticeResponse(
                notice.getId(),
                notice.getClub().getId(),
                notice.getClub().getName(),
                notice.getTitle(),
                notice.getContent(),
                notice.getNoticeDate(),
                notice.getBadge(),
                notice.isPinned()
        );
    }
}
