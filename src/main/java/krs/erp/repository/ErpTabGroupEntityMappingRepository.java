package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import krs.erp.model.ErpTabGroupEntityMapping;

@Repository
public interface ErpTabGroupEntityMappingRepository extends JpaRepository<ErpTabGroupEntityMapping, Long> {

    List<ErpTabGroupEntityMapping> findByTabGroupIdOrderBySequenceAsc(Long tabGroupId);

    Optional<ErpTabGroupEntityMapping> findByTabGroupIdAndEntityId(Long tabGroupId, Long entityId);

    void deleteByTabGroupId(Long tabGroupId);

    boolean existsByTabGroupIdAndEntityId(Long tabGroupId, Long entityId);
}
