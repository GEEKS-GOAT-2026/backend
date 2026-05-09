package geeks.dongnea.domain.club.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "clubs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String activityDescription;

    private String category;

    private String profileImg;

    private String contact;

    private String instagramUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Club(
            String name,
            String description,
            String activityDescription,
            String category,
            String profileImg,
            String contact,
            String instagramUrl
    ) {
        this.name = name;
        this.description = description;
        this.activityDescription = activityDescription;
        this.category = category;
        this.profileImg = profileImg;
        this.contact = contact;
        this.instagramUrl = instagramUrl;
    }
}
