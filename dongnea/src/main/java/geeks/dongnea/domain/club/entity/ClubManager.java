package geeks.dongnea.domain.club.entity;

import geeks.dongnea.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "club_managers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "club_id"}) // 중복 등록 방지
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubManager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(nullable = false)
    private String role; // 예: "PRESIDENT", "VICE_PRESIDENT"

    @Builder
    public ClubManager(User user, Club club, String role) {
        this.user = user;
        this.club = club;
        this.role = role;
    }

    public void updateRole(String role) {
        this.role = role;
    }
}
