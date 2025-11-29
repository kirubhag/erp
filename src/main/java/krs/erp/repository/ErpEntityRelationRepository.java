package krs.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.ErpEntityRelation;

/**
 * Repository interface for ErpEntityRelation entity.
 * Provides data access methods for managing relationships between ERP entities.
 */
@Repository
public interface ErpEntityRelationRepository extends JpaRepository<ErpEntityRelation, Long> {

    /**
     * Find all relationships where the given table is a parent
     */
    List<ErpEntityRelation> findByParentTableName(String parentTableName);

    /**
     * Find all relationships where the given table is a child
     */
    List<ErpEntityRelation> findByChildTableName(String childTableName);

    /**
     * Find a specific relationship between parent and child tables
     */
    @Query("SELECT r FROM ErpEntityRelation r " +
           "WHERE r.parentTableName = :parentTable AND r.childTableName = :childTable")
    List<ErpEntityRelation> findRelationship(@Param("parentTable") String parentTable, 
                                              @Param("childTable") String childTable);

    /**
     * Find all child relationships for a parent table
     */
    @Query("SELECT r FROM ErpEntityRelation r " +
           "WHERE r.parentTableName = :parentTable " +
           "ORDER BY r.childTableName")
    List<ErpEntityRelation> findAllChildRelationships(@Param("parentTable") String parentTable);

    /**
     * Check if a relationship exists between parent and child tables
     */
    @Query("SELECT COUNT(r) > 0 FROM ErpEntityRelation r " +
           "WHERE r.parentTableName = :parentTable AND r.childTableName = :childTable")
    boolean relationshipExists(@Param("parentTable") String parentTable, 
                                @Param("childTable") String childTable);
}
