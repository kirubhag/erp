package krs.erp.repository.library;

import krs.erp.model.User;
import krs.erp.model.library.LibraryLoan;
import krs.erp.model.library.ResourceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryLoanRepository extends JpaRepository<LibraryLoan, Long> {
    List<LibraryLoan> findByStatus(LibraryLoan.LoanStatus status);

    List<LibraryLoan> findByUserAndStatus(User user, LibraryLoan.LoanStatus status);

    List<LibraryLoan> findByItemAndStatus(ResourceItem item, LibraryLoan.LoanStatus status);
}
