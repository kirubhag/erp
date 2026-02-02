package krs.erp.repository.library;

import krs.erp.model.library.LibraryFineRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryFineRuleRepository extends JpaRepository<LibraryFineRule, Long> {
    Optional<LibraryFineRule> findByMemberType(String memberType);
}
