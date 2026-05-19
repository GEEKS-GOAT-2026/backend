package geeks.dongnea.domain.club.dto;

import geeks.dongnea.domain.club.entity.ClubMember;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ClubMemberResponse {

    private final Long id;
    private final String name;
    private final String major;
    private final String email;
    private final LocalDate birth;
    private final String phone;
    private final String image;
    private final String status;

    private ClubMemberResponse(
            Long id,
            String name,
            String major,
            String email,
            LocalDate birth,
            String phone,
            String image,
            String status
    ) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.email = email;
        this.birth = birth;
        this.phone = phone;
        this.image = image;
        this.status = status;
    }

    public static ClubMemberResponse from(ClubMember member) {
        return new ClubMemberResponse(
                member.getId(),
                member.getName(),
                member.getMajor(),
                member.getEmail(),
                member.getBirth(),
                member.getPhone(),
                member.getImage(),
                member.getStatus()
        );
    }
}
