package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.ErpTabGroup;

@Repository
public interface ErpTabGroupRepository extends JpaRepository<ErpTabGroup, Long> {

    Optional<ErpTabGroup> findByCode(String code);

    Optional<ErpTabGroup> findByName(String name);

    List<ErpTabGroup> findByIsActiveOrderBySequenceAsc(Integer isActive);

    boolean existsByCode(String code);
}
