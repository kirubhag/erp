package krs.erp.repository.lms;

import krs.erp.model.lms.LmsForumPost;
import krs.erp.model.lms.LmsForum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LmsForumPostRepository extends JpaRepository<LmsForumPost, Long> {
    List<LmsForumPost> findByForum(LmsForum forum);
}
