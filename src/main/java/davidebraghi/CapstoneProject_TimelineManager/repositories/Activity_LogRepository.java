package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.Activity_Log;
import davidebraghi.CapstoneProject_TimelineManager.enums.ActivityTypeENUM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Activity_LogRepository extends JpaRepository<Activity_Log, Long> {

    // cerca lo user in base al suo ID

    List<Activity_Log> findByUser_UserId(Long userId);

    // cerca il progetto in base al suo ID

    List<Activity_Log> findByProject_ProjectId(Long projectId);

    // cerca il task in base al suo ID

    List<Activity_Log> findByTask_TaskId(Long taskId);

    // cerca l'attività in base al suo tipo

    List<Activity_Log> findByActivityType(ActivityTypeENUM activityTypeENUM);

    // conta il numero di attività svolte da uno specifico user

    Long countByUser_UserId(Long userId);
}