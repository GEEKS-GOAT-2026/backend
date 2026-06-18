package geeks.dongnea.domain.club.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        this.phone = phone;
        this.image = image;
        this.status = status;
    }

    public void accept() {
        this.status = "member";
    }

    public void markApplicant() {
        this.status = "applicant";
    }

    public void updateApplicantInfo(
            String name,
            String studentNumber,
            String department,
            String major,
            String phone,
            String image
    ) {
        this.name = name;
        this.studentNumber = studentNumber;
        this.department = department;
        this.major = major;
        this.phone = phone;
        this.image = image;
    }

}
