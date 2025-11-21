package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.Task_Assignee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Task_AssigneeRepository extends JpaRepository<Task_Assignee, Long> {

    // cerca tutti gli users assegnati ad uno specifico task

//    List<Task_Assignee> findByTask_TaskId(Long projectId, Long taskId);

    // cerca tutti i tasks assegnati ad uno specifico user

    List<Task_Assignee> findByUser_UserId(Long userId);

    // verifica se uno specifico user è assegnato ad uno specifico task

    Optional<Task_Assignee> findByTask_TaskIdAndUser_UserId(Long taskId,
                                                            Long userId);

    // conta quanti users sono assegnati ad uno specifico task

    Long countByTask_TaskId(Long taskId);

    // conta quanti tasks sono assegnati ad uno specifico user

    Long countByUser_UserId(Long userId);
}