package krs.erp.repository.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import krs.erp.model.finance.FineConfiguration;

import java.util.List;

@Repository
public interface FineConfigurationRepository extends JpaRepository<FineConfiguration, Long> {
    List<FineConfiguration> findByCategoryId(Long categoryId);
}
