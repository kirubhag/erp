package krs.erp.model.alumni;

import javax.persistence.*;
import krs.erp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "erp_alumni_profiles")
@Data
@EqualsAndHashCode(callSuper = true)
public class Alumni extends BaseEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "degree")
    private String degree;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "company")
    private String company;

    @Column(name = "linkedin_profile")
    private String linkedinProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AlumniStatus status = AlumniStatus.ACTIVE;

    public enum AlumniStatus {
        ACTIVE, INACTIVE
    }
}
