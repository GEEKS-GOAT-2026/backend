package geeks.dongnea.domain.club.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "recruitments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private boolean isAlwaysOpen = false;

    // 질문 양식을 JSONB로 저장 (예: {"질문1": "지원동기", "타입": "text"})
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> formSchema;

    @Column(nullable = false)
    private boolean isActive = true; // 모집 중인지 여부

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Recruitment(
            Club club,
            String title,
            String summary,
            LocalDate startDate,
            LocalDate endDate,
            Boolean isAlwaysOpen,
            Map<String, Object> formSchema,
            Boolean isActive
    ) {
        this.club = club;
        this.title = title;
        this.summary = summary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isAlwaysOpen = Boolean.TRUE.equals(isAlwaysOpen);
        this.formSchema = formSchema;
        this.isActive = isActive == null || Boolean.TRUE.equals(isActive);
    }

    public boolean isOpenOn(LocalDate today) {
        if (!isActive) {
            return false;
        }

        if (isAlwaysOpen) {
            return true;
        }

        boolean started = startDate == null || !startDate.isAfter(today);
        boolean notEnded = endDate == null || !endDate.isBefore(today);

        return started && notEnded;
    }
}
