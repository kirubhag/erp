package krs.erp.repository.library;

import krs.erp.model.Role;
import krs.erp.model.library.LibraryPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibraryPolicyRepository extends JpaRepository<LibraryPolicy, Long> {
    Optional<LibraryPolicy> findByRoleAndGradeLevel(Role role, String gradeLevel);

    Optional<LibraryPolicy> findByRole(Role role);
}
