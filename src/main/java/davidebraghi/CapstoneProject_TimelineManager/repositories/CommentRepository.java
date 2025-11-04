package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // cerca tutti i commenti scritti su uno specifico task

    List<Comment> findByTask_TaskId(Long taskId);

    // cerca tutti i commenti scritti su uno specifico progetto

    List<Comment> findByProject_ProjectId(Long projectId);

    // cerca tutti i commenti scritti da uno specifico user

    List<Comment> findByUser_UserId(Long userId);

    // conta il numero di commenti su uno specifico task

    Long countByTask_TaskId(Long taskId);

    // conta il numero di commenti su uno specifico progetto

    Long countByProject_ProjectId(Long projectId);

    // conta il numero di commenti da uno specifico user

    Long countByUser_UserId(Long userId);
}