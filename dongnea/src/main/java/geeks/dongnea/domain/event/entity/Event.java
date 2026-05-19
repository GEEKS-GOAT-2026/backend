package geeks.dongnea.domain.event.entity;

import geeks.dongnea.domain.club.entity.Club;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

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
    private LocalDate eventDate;

    private String location;

    private String imageUrl;

    @Column(nullable = false)
    private boolean published = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Event(
            Club club,
            String title,
            String description,
            LocalDate eventDate,
            String location,
            String imageUrl,
            Boolean published
    ) {
        this.club = club;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.imageUrl = imageUrl;
        this.published = published == null || published;
    }
}
