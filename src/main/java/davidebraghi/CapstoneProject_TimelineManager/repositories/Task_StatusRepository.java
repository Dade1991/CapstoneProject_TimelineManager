package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.Task_Status;
import davidebraghi.CapstoneProject_TimelineManager.enums.TaskStatusENUM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Task_StatusRepository extends JpaRepository<Task_Status, Long> {

    // cerca uno specifico status per il suo id

    Optional<Task_Status> findByTaskStatusId(Long taskStatusId);

    // cerca uno specifico status per il suo nome

    Optional<Task_Status> findByStatusName(TaskStatusENUM statusName);

    // cerca tutti gli status ordinati

    List<Task_Status> findAllByOrderByOrderIndexAsc();

    // verifica se esiste uno specifico status

    boolean existsByStatusName(TaskStatusENUM statusName);
}