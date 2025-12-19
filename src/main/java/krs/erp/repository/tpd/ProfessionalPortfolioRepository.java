package krs.erp.repository.tpd;

import krs.erp.model.tpd.ProfessionalPortfolio;
import krs.erp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfessionalPortfolioRepository extends JpaRepository<ProfessionalPortfolio, Long> {
    Optional<ProfessionalPortfolio> findByStaff(User staff);
}
