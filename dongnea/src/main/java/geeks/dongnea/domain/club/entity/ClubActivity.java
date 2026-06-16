package geeks.dongnea.domain.club.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "club_activities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    private String imageUrl;

    @Builder
    public ClubActivity(
            Club club,
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String imageUrl
    ) {
        this.club = club;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
    }

    public void update(
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String imageUrl
    ) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }
}
