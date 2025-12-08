package krs.erp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.LoginHistory;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    List<LoginHistory> findByUserId(Long userId);

    Page<LoginHistory> findByUserId(Long userId, Pageable pageable);

    Page<LoginHistory> findByUsername(String username, Pageable pageable);

    @org.springframework.lang.NonNull
    Page<LoginHistory> findAll(@org.springframework.lang.NonNull Pageable pageable);
}
