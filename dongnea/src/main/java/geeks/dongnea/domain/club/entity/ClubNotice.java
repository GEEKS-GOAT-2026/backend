package geeks.dongnea.domain.club.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "club_notices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDate noticeDate;

    private String badge;

    @Column(nullable = false)
    private boolean pinned;

    @Builder
    public ClubNotice(
            Club club,
            String title,
            String content,
            LocalDate noticeDate,
            String badge,
            Boolean pinned
    ) {
        this.club = club;
        this.title = title;
        this.content = content;
        this.noticeDate = noticeDate;
        this.badge = badge;
        this.pinned = pinned != null && pinned;
    }

    public void update(
            String title,
            String content,
            LocalDate noticeDate,
            String badge,
            Boolean pinned
    ) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (noticeDate != null) {
            this.noticeDate = noticeDate;
        }
        if (badge != null) {
            this.badge = badge;
        }
        if (pinned != null) {
            this.pinned = pinned;
        }
    }
}
