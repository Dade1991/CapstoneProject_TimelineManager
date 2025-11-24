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

    // recupera la posizione massima delle task di un progetto

    @Query("SELECT MAX(t.position) FROM Task t WHERE t.project.projectId = :projectId")
    Integer findMaxPositionByProjectId(@Param("projectId") Long projectId);

    // cerca i task di uno specifico progetto CON categorie caricate (JOIN FETCH)

    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.categories WHERE t.project.projectId = :projectId")
    List<Task> findByProjectIdWithCategories(@Param("projectId") Long projectId);

    // cerca i task in uno specifico status

    List<Task> findByProject_ProjectIdAndStatus_TaskStatusId(Long projectId, Long statusId);

    // cerca task per id, progetto e categoria (modifica aggiunta)

    @Query("SELECT DISTINCT t FROM Task t JOIN t.categories c WHERE t.project.projectId = :projectId AND c.categoryId = :categoryId")
    List<Task> findByProjectIdAndCategoryId(@Param("projectId") Long projectId,
                                            @Param("categoryId") Long categoryId);

    // cerca task per Id progetto e user Id

    @Query("SELECT t FROM Task t JOIN t.assignees a WHERE t.project.projectId = :projectId AND a.user.userId = :userId")
    List<Task> findTasksByProjectAndUser(@Param("projectId") Long projectId,
                                         @Param("userId") Long userId);
    // cerca task con paginazione e categoria

    Page<Task> findDistinctByCategories_CategoryIdIn(List<Long> categoryIds, Pageable pageable);

    // cerca task per id e progetto (per il servizio) [ORDINE GIUSTO]

    Optional<Task> findByProject_ProjectIdAndTaskId(Long projectId, Long taskId);

    // conta quanti tasks sono presenti in uno specifico progetto

    long countByProject_ProjectId(Long projectId);

    // conta quanti tasks sono stati creati da uno specifico user

    long countByCreator_UserId(Long userId);

    // cerca tutti i task di uno specifico progetto (paginato)

    Page<Task> findByProject_ProjectId(Long projectId, Pageable pageable);

    // cerca tutti i task ordinati per position

    List<Task> findByProject_ProjectIdOrderByPositionAsc(Long projectId);

    //    _______________________________________________________

    // conta le task non completate di un progetto e categoria specifica

    @Query("SELECT COUNT(t) FROM Task t JOIN t.categories c WHERE t.project.projectId = :projectId AND c.categoryId = :categoryId AND t.completedAt IS NULL")
    long countByProjectIdAndCategoryIdAndCompletedAtIsNull(@Param("projectId") Long projectId, @Param("categoryId") Long categoryId);

    // conta le task completate di un progetto e categoria specifica

    @Query("SELECT COUNT(t) FROM Task t JOIN t.categories c WHERE " +
            "t.project.projectId = :projectId AND c.categoryId = :categoryId AND t.completedAt IS NOT NULL")
    long countByProjectIdAndCategoryIdAndCompletedAtIsNotNull(@Param("projectId") Long projectId, @Param("categoryId") Long categoryId);

    // conta tutte le task di un progetto e categoria specifica

    @Query("SELECT COUNT(t) FROM Task t JOIN t.categories c WHERE t.project.projectId = :projectId AND c.categoryId = :categoryId")
    long countByProjectIdAndCategoryId(@Param("projectId") Long projectId, @Param("categoryId") Long categoryId);

    // conta quanti tasks non sono stati completati globalmente

    long countByProject_ProjectIdAndCompletedAtIsNull(Long projectId);

    // conta quanti tasks sono stati completati globalmente

    long countByProject_ProjectIdAndCompletedAtIsNotNull(Long projectId);

    // cerca task scaduti in un progetto (indipendentemente da categoria)

    List<Task> findByProject_ProjectIdAndTaskExpiryDateBefore(Long projectId, LocalDate date);

    // cerca task non scaduti in un progetto

    List<Task> findByProject_ProjectIdAndTaskExpiryDateAfter(Long projectId, LocalDate date);

    // cerca task filtrato per categoria

    @Query("SELECT t FROM Task t JOIN t.categories c WHERE t.project.projectId = :projectId AND c.categoryId = :categoryId AND t.taskId = :taskId")
    Optional<Task> findByProject_ProjectIdAndCategoryIdAndTaskId(@Param("projectId") Long projectId,
                                                                 @Param("categoryId") Long categoryId,
                                                                 @Param("taskId") Long taskId);
}