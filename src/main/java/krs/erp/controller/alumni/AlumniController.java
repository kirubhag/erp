package krs.erp.controller.alumni;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import krs.erp.model.alumni.Alumni;
import krs.erp.model.alumni.AlumniContribution;
import krs.erp.service.alumni.AlumniService;
import java.util.List;

@RestController
@RequestMapping("/api/alumni")
public class AlumniController {

    @Autowired
    private AlumniService alumniService;

    @GetMapping("/profiles")
    public List<Alumni> getAllAlumni() {
        return alumniService.getAllAlumni();
    }

    @PostMapping("/profiles")
    public Alumni createAlumni(@RequestBody Alumni alumni) {
        return alumniService.saveAlumni(alumni);
    }

    @GetMapping("/contributions/{alumniId}")
    public List<AlumniContribution> getContributions(@PathVariable Long alumniId) {
        return alumniService.getContributionsByAlumni(alumniId);
    }

    @PostMapping("/contributions")
    public AlumniContribution createContribution(@RequestBody AlumniContribution contribution) {
        return alumniService.saveContribution(contribution);
    }
}
