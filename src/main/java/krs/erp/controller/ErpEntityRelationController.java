package krs.erp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import krs.erp.model.ErpEntityRelation;
import krs.erp.service.ErpEntityRelationService;

/**
 * REST Controller for managing ERP entity relationships.
 * Provides endpoints for CRUD operations and dynamic query execution.
 */
@RestController
@RequestMapping("/api/entity-relations")
public class ErpEntityRelationController {

    @Autowired
    private ErpEntityRelationService relationService;

    /**
     * Create a new entity relationship
     * POST /api/entity-relations
     */
    @PostMapping
    public ResponseEntity<ErpEntityRelation> createRelationship(@Valid @RequestBody ErpEntityRelation relation) {
        try {
            ErpEntityRelation created = relationService.createRelationship(relation);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing relationship
     * PUT /api/entity-relations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ErpEntityRelation> updateRelationship(
            @PathVariable Long id,
            @Valid @RequestBody ErpEntityRelation relation) {
        try {
            ErpEntityRelation updated = relationService.updateRelationship(id, relation);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a relationship
     * DELETE /api/entity-relations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteRelationship(@PathVariable Long id) {
        try {
            relationService.deleteRelationship(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a relationship by ID
     * GET /api/entity-relations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ErpEntityRelation> getRelationshipById(@PathVariable Long id) {
        return relationService.getRelationshipById(id)
                .map(relation -> new ResponseEntity<>(relation, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get all relationships
     * GET /api/entity-relations
     */
    @GetMapping
    public ResponseEntity<List<ErpEntityRelation>> getAllRelationships() {
        try {
            List<ErpEntityRelation> relations = relationService.getAllRelationships();
            if (relations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(relations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all child relationships for a parent table
     * GET /api/entity-relations/parent/{tableName}
     */
    @GetMapping("/parent/{tableName}")
    public ResponseEntity<List<ErpEntityRelation>> getChildRelationships(@PathVariable String tableName) {
        try {
            List<ErpEntityRelation> relations = relationService.getChildRelationships(tableName);
            if (relations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(relations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all parent relationships for a child table
     * GET /api/entity-relations/child/{tableName}
     */
    @GetMapping("/child/{tableName}")
    public ResponseEntity<List<ErpEntityRelation>> getParentRelationships(@PathVariable String tableName) {
        try {
            List<ErpEntityRelation> relations = relationService.getParentRelationships(tableName);
            if (relations.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(relations, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch child data with parent information using dynamic join
     * GET /api/entity-relations/query/child-with-parent
     * Example: /api/entity-relations/query/child-with-parent?childTable=student_guardian_info&childId=1
     */
    @GetMapping("/query/child-with-parent")
    public ResponseEntity<List<Map<String, Object>>> fetchChildWithParent(
            @RequestParam String childTable,
            @RequestParam(required = false) Long childId) {
        try {
            List<Map<String, Object>> results = relationService.fetchChildWithParent(childTable, childId);
            if (results.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetch parent data with all child records using dynamic joins
     * GET /api/entity-relations/query/parent-with-children
     * Example: /api/entity-relations/query/parent-with-children?parentTable=students&parentId=1
     */
    @GetMapping("/query/parent-with-children")
    public ResponseEntity<Map<String, Object>> fetchParentWithChildren(
            @RequestParam String parentTable,
            @RequestParam Long parentId) {
        try {
            Map<String, Object> result = relationService.fetchParentWithChildren(parentTable, parentId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get the dynamic SQL query for fetching child with parent
     * GET /api/entity-relations/query/sql/{childTable}
     */
    @GetMapping("/query/sql/{childTable}")
    public ResponseEntity<Map<String, String>> getQuerySQL(@PathVariable String childTable) {
        try {
            String sql = relationService.buildChildWithParentQuery(childTable);
            return new ResponseEntity<>(Map.of("sql", sql), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Specific endpoint to fetch Guardian with Student information
     * GET /api/entity-relations/guardian/{id}/with-student
     */
    @GetMapping("/guardian/{id}/with-student")
    public ResponseEntity<List<Map<String, Object>>> fetchGuardianWithStudent(@PathVariable Long id) {
        try {
            List<Map<String, Object>> results = relationService.fetchGuardianWithStudent(id);
            if (results.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Specific endpoint to fetch Medical with Student information
     * GET /api/entity-relations/medical/{id}/with-student
     */
    @GetMapping("/medical/{id}/with-student")
    public ResponseEntity<List<Map<String, Object>>> fetchMedicalWithStudent(@PathVariable Long id) {
        try {
            List<Map<String, Object>> results = relationService.fetchMedicalWithStudent(id);
            if (results.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Specific endpoint to fetch Student with all Guardian and Medical records
     * GET /api/entity-relations/student/{id}/with-children
     */
    @GetMapping("/student/{id}/with-children")
    public ResponseEntity<Map<String, Object>> fetchStudentWithChildren(@PathVariable Long id) {
        try {
            Map<String, Object> result = relationService.fetchStudentWithChildren(id);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
