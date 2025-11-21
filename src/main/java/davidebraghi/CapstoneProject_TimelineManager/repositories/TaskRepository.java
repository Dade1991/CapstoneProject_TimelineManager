package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    // cerca tutti i task di uno specifico progetto

    List<Task> findByProject_ProjectId(Long projectId);

    // cerca tutti i task di uno specifico progetto (paginato)

    Page<Task> findByProject_ProjectId(Long projectId, Pageable pageable);

    // cerca i task di uno specifico progetto CON categorie caricate (JOIN FETCH)

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.categories WHERE t.project.projectId = :projectId")
    List<Task> findByProjectIdWithCategories(@Param("projectId") Long projectId);

    // cerca i task in uno specifico status

    List<Task> findByProject_ProjectIdAndStatus_TaskStatusId(Long projectId, Long statusId);

    // cerca i task creati da uno specifico user

//    List<Task> findByTask_Project_ProjectIdAndUser_UserId(Long projectId, Long userId);

//    List<Task> findByProject_ProjectIdAndUser_UserId(Long projectId, Long userId);

    // cerca task con paginazione e categoria

    Page<Task> findDistinctByCategories_CategoryIdIn(List<Long> categoryIds, Pageable pageable);

    // cerca task per id e progetto (per il servizio)

    Optional<Task> findByTaskIdAndProject_ProjectId(Long taskId, Long projectId); // Per la ricerca con doppio filtro

    // conta quanti tasks sono presenti in uno specifico progetto

    long countByProject_ProjectId(Long projectId);

    // cerca i task non completati

    List<Task> findByProject_ProjectIdAndCompletedAtIsNull(Long projectId);

    // cerca i task completati

    List<Task> findByProject_ProjectIdAndCompletedAtIsNotNull(Long projectId);

    //    _______________________________________________________

    // cerca i task scaduti

    List<Task> findByTaskExpiryDateBefore(LocalDate date);

    // cerca i task non ancora scaduti

    List<Task> findByTaskExpiryDateAfter(LocalDate date);


    // conta quanti tasks sono stati creati da uno specifico user

    long countByCreator_UserId(Long userId);

    // conta quanti tasks non sono stati completati globalmente

    long countByCompletedAtIsNull();

}