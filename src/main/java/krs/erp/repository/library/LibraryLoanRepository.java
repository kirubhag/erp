package krs.erp.repository.library;

import krs.erp.model.User;
import krs.erp.model.library.LibraryLoan;
import krs.erp.model.library.ResourceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryLoanRepository extends JpaRepository<LibraryLoan, Long> {
    List<LibraryLoan> findByStatus(LibraryLoan.LoanStatus status);

    List<LibraryLoan> findByUserAndStatus(User user, LibraryLoan.LoanStatus status);

    List<LibraryLoan> findByItemAndStatus(ResourceItem item, LibraryLoan.LoanStatus status);

    long countByStatus(LibraryLoan.LoanStatus status);

    /**
     * Get top borrowed books (trending) by counting loans per resource
     * Returns: resourceId, title, borrowCount
     */
    @Query("SELECT l.item.resource.id, l.item.resource.title, COUNT(l) as borrowCount " +
           "FROM LibraryLoan l " +
           "GROUP BY l.item.resource.id, l.item.resource.title " +
           "ORDER BY borrowCount DESC")
    List<Object[]> findTopBorrowedBooks();

    /**
     * Get users with overdue items count (overdue leaders)
     * Returns: userId, firstName, lastName, overdueCount
     */
    @Query("SELECT l.user.id, l.user.firstName, l.user.lastName, COUNT(l) as overdueCount " +
           "FROM LibraryLoan l " +
           "WHERE l.status = 'OVERDUE' " +
           "GROUP BY l.user.id, l.user.firstName, l.user.lastName " +
           "ORDER BY overdueCount DESC")
    List<Object[]> findOverdueLeaders();
}
