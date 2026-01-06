package krs.erp.repository.finance;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.finance.ChartOfAccount;

@Repository
public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, Long> {
    Optional<ChartOfAccount> findByName(String name);
}
