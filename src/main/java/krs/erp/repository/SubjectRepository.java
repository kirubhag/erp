package krs.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import krs.erp.model.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findBySubjectCode(String subjectCode);

    List<Subject> findByGradeLevel(String gradeLevel);

    List<Subject> findByCategory(String category);

    List<Subject> findByIsActive(Integer isActive);

    List<Subject> findByGradeLevelAndIsActive(String gradeLevel, Integer isActive);

    List<Subject> findByCategoryAndIsActive(String category, Integer isActive);

    @Query("SELECT s FROM Subject s WHERE s.isActive = 1 ORDER BY s.gradeLevel, s.subjectName")
    List<Subject> findAllActiveSubjects();

    @Query("SELECT s FROM Subject s WHERE " +
           "(LOWER(s.subjectCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.gradeLevel) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.category) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Subject> searchSubjects(@Param("search") String search);

    @Query("SELECT COUNT(s) FROM Subject s WHERE s.gradeLevel = :gradeLevel AND s.isActive = 1")
    long countByGradeLevelAndActive(@Param("gradeLevel") String gradeLevel);

    boolean existsBySubjectCode(String subjectCode);
}
