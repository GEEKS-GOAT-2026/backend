package geeks.dongnea.domain.club.dto;

import geeks.dongnea.domain.club.entity.ClubMember;
import lombok.Getter;

@Getter
public class ClubMemberResponse {

    private final Long id;
    private final String name;
    private final String studentNumber;
    private final String department;
    private final String phone;
    private final String email;
    private final String image;
    private final String status;

    private ClubMemberResponse(
            Long id,
            String name,
            String studentNumber,
            String department,
            String phone,
            String email,
            String image,
            String status
    ) {
        this.id = id;
        this.name = name;
        this.studentNumber = studentNumber;
        this.department = department;
        this.phone = phone;
        this.email = email;
        this.image = image;
        this.status = status;
    }

    public static ClubMemberResponse from(ClubMember member) {
        return new ClubMemberResponse(
                member.getId(),
                member.getName(),
                member.getStudentNumber(),
                member.getDepartment(),
                member.getPhone(),
                member.getEmail(),
                member.getImage(),
                member.getStatus()
        );
    }
}
