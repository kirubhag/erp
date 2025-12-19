package krs.erp.repository.lms;

import krs.erp.model.lms.LmsStudentProgress;
import krs.erp.model.lms.Lesson;
import krs.erp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LmsStudentProgressRepository extends JpaRepository<LmsStudentProgress, Long> {
    List<LmsStudentProgress> findByStudent(User student);

    Optional<LmsStudentProgress> findByStudentAndLesson(User student, Lesson lesson);
}
