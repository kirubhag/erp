package krs.erp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import krs.erp.model.ErpEntityRelation;
import krs.erp.repository.ErpEntityRelationRepository;

/**
 * Service for managing ERP entity relationships and dynamic query building.
 * Provides methods to create, update, delete relationships and build dynamic queries
 * based on relationship metadata.
 */
@Service
@Transactional
public class ErpEntityRelationService {

    @Autowired
    private ErpEntityRelationRepository relationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Create a new entity relationship
     */
    public ErpEntityRelation createRelationship(ErpEntityRelation relation) {
        return relationRepository.save(relation);
    }

    /**
     * Update an existing relationship
     */
    public ErpEntityRelation updateRelationship(Long id, ErpEntityRelation relation) {
        Optional<ErpEntityRelation> existing = relationRepository.findById(id);
        if (existing.isPresent()) {
            ErpEntityRelation updated = existing.get();
            updated.setParentTableName(relation.getParentTableName());
            updated.setParentPkid(relation.getParentPkid());
            updated.setParentDisplayColumn(relation.getParentDisplayColumn());
            updated.setChildTableName(relation.getChildTableName());
            updated.setChildPkid(relation.getChildPkid());
            updated.setChildDisplayColumn(relation.getChildDisplayColumn());
            updated.setForeignKeyColumn(relation.getForeignKeyColumn());
            updated.setDescription(relation.getDescription());
            return relationRepository.save(updated);
        }
        throw new RuntimeException("Relationship not found with id: " + id);
    }

    /**
     * Delete a relationship by ID
     */
    public void deleteRelationship(Long id) {
        relationRepository.deleteById(id);
    }

    /**
     * Get a relationship by ID
     */
    public Optional<ErpEntityRelation> getRelationshipById(Long id) {
        return relationRepository.findById(id);
    }

    /**
     * Get all relationships
     */
    public List<ErpEntityRelation> getAllRelationships() {
        return relationRepository.findAll();
    }

    /**
     * Get all child relationships for a parent table
     */
    public List<ErpEntityRelation> getChildRelationships(String parentTableName) {
        return relationRepository.findByParentTableName(parentTableName);
    }

    /**
     * Get all parent relationships for a child table
     */
    public List<ErpEntityRelation> getParentRelationships(String childTableName) {
        return relationRepository.findByChildTableName(childTableName);
    }

    /**
     * Check if a relationship exists between parent and child
     */
    public boolean relationshipExists(String parentTable, String childTable) {
        return relationRepository.relationshipExists(parentTable, childTable);
    }

    /**
     * Build and execute a dynamic query to fetch child data with parent information
     * Example: Fetch Guardian data with Student information joined
     * 
     * @param childTableName The child table name (e.g., "student_guardian_info")
     * @param childId Optional child record ID to filter by
     * @return List of maps containing joined data
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> fetchChildWithParent(String childTableName, Long childId) {
        List<ErpEntityRelation> relations = relationRepository.findByChildTableName(childTableName);
        
        if (relations.isEmpty()) {
            throw new RuntimeException("No relationship found for child table: " + childTableName);
        }

        // Use the first relationship (in real scenarios, you might want to handle multiple)
        ErpEntityRelation relation = relations.get(0);
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("c.").append(relation.getChildPkid()).append(" AS child_id, ");
        sql.append("c.").append(relation.getChildDisplayColumn()).append(" AS child_display, ");
        sql.append("p.").append(relation.getParentPkid()).append(" AS parent_id, ");
        sql.append("p.").append(relation.getParentDisplayColumn()).append(" AS parent_display, ");
        sql.append("c.* ");
        sql.append("FROM ").append(relation.getChildTableName()).append(" c ");
        sql.append("INNER JOIN ").append(relation.getParentTableName()).append(" p ");
        sql.append("ON c.").append(relation.getForeignKeyColumn()).append(" = p.").append(relation.getParentPkid());
        
        if (childId != null) {
            sql.append(" WHERE c.").append(relation.getChildPkid()).append(" = :childId");
        }

        Query query = entityManager.createNativeQuery(sql.toString());
        if (childId != null) {
            query.setParameter("childId", childId);
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        
        return convertResultsToMap(results, query);
    }

    /**
     * Build and execute a dynamic query to fetch parent data with all child records
     * Example: Fetch Student with all Guardian and Medical records
     * 
     * @param parentTableName The parent table name (e.g., "students")
     * @param parentId The parent record ID
     * @return Map containing parent data and all child records grouped by child table
     */
    @Transactional(readOnly = true)
    public Map<String, Object> fetchParentWithChildren(String parentTableName, Long parentId) {
        List<ErpEntityRelation> relations = relationRepository.findByParentTableName(parentTableName);
        
        if (relations.isEmpty()) {
            throw new RuntimeException("No relationships found for parent table: " + parentTableName);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("parentTable", parentTableName);
        result.put("parentId", parentId);

        // Fetch each child relationship
        for (ErpEntityRelation relation : relations) {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT c.* FROM ");
            sql.append(relation.getChildTableName()).append(" c ");
            sql.append("WHERE c.").append(relation.getForeignKeyColumn()).append(" = :parentId");

            Query query = entityManager.createNativeQuery(sql.toString());
            query.setParameter("parentId", parentId);

            @SuppressWarnings("unchecked")
            List<Object[]> childResults = query.getResultList();
            
            result.put(relation.getChildTableName(), convertResultsToMap(childResults, query));
        }

        return result;
    }

    /**
     * Build a dynamic SQL query string for fetching child with parent
     * This is useful for debugging or generating query templates
     */
    public String buildChildWithParentQuery(String childTableName) {
        List<ErpEntityRelation> relations = relationRepository.findByChildTableName(childTableName);
        
        if (relations.isEmpty()) {
            return "No relationship found for table: " + childTableName;
        }

        ErpEntityRelation relation = relations.get(0);
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.*, p.* FROM ");
        sql.append(relation.getChildTableName()).append(" c ");
        sql.append("INNER JOIN ").append(relation.getParentTableName()).append(" p ");
        sql.append("ON c.").append(relation.getForeignKeyColumn());
        sql.append(" = p.").append(relation.getParentPkid());
        
        return sql.toString();
    }

    /**
     * Convert native query results to a list of maps
     */
    private List<Map<String, Object>> convertResultsToMap(List<Object[]> results, Query query) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        if (results.isEmpty()) {
            return mapList;
        }

        // Get column names from query metadata (simplified approach)
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < row.length; i++) {
                map.put("column_" + i, row[i]);
            }
            mapList.add(map);
        }
        
        return mapList;
    }

    /**
     * Fetch Guardian data with Student information
     * This is a specific implementation using the generic method
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> fetchGuardianWithStudent(Long guardianId) {
        return fetchChildWithParent("student_guardian_info", guardianId);
    }

    /**
     * Fetch Medical data with Student information
     * This is a specific implementation using the generic method
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> fetchMedicalWithStudent(Long medicalId) {
        return fetchChildWithParent("student_medical_info", medicalId);
    }

    /**
     * Fetch Student with all Guardian and Medical records
     * This is a specific implementation using the generic method
     */
    @Transactional(readOnly = true)
    public Map<String, Object> fetchStudentWithChildren(Long studentId) {
        return fetchParentWithChildren("students", studentId);
    }
}
