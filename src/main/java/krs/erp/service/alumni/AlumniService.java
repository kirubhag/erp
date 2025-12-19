package krs.erp.service.alumni;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import krs.erp.model.alumni.Alumni;
import krs.erp.model.alumni.AlumniContribution;
import krs.erp.repository.alumni.AlumniRepository;
import krs.erp.repository.alumni.AlumniContributionRepository;
import java.util.List;

@Service
@Transactional
public class AlumniService {

    @Autowired
    private AlumniRepository alumniRepository;

    @Autowired
    private AlumniContributionRepository contributionRepository;

    public List<Alumni> getAllAlumni() {
        return alumniRepository.findAll();
    }

    public Alumni saveAlumni(Alumni alumni) {
        return alumniRepository.save(alumni);
    }

    public List<AlumniContribution> getContributionsByAlumni(Long alumniId) {
        return contributionRepository.findByAlumniId(alumniId);
    }

    public AlumniContribution saveContribution(AlumniContribution contribution) {
        return contributionRepository.save(contribution);
    }
}
