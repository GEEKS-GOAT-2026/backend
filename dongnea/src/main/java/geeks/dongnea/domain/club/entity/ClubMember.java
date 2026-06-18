package geeks.dongnea.domain.club.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "club_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(nullable = false)
    private String name;

    @Column(length = 8)
    private String studentNumber;

    private String department;

    private String major;

    @Column(nullable = false)
    private String email;

    private LocalDate birth;

    private String phone;

    private String image;

    @Column(nullable = false)
    private String status;

    @Builder
    public ClubMember(
            Club club,
            String name,
            String studentNumber,
            String department,
            String major,
            String email,
            LocalDate birth,
            String phone,
            String image,
            String status
    ) {
        this.club = club;
        this.name = name;
        this.studentNumber = studentNumber;
        this.department = department;
        this.major = major;
        this.email = email;
        this.birth = birth;
        this.phone = phone;
        this.image = image;
        this.status = status;
    }

    public void accept() {
        this.status = "member";
    }

    public void reject() {
        this.status = "rejected";
    }

    public void leave() {
        this.status = "left";
    }
}
