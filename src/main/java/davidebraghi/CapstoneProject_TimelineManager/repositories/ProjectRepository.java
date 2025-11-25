package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // metodo per recuperare tutti i progetti ordinati per creationDate ascendente

    List<Project> findAllByOrderByCreationDateAsc();

    // cerca i progetti per uno specifico creatore(user)

    List<Project> findByCreator_UserId(Long userId);

    // prende tutti i progetti, associa i membri ai progetti (LEFT JOIN) e restituisce tutti i membri ai relativi progetti (projectRepository)

    @Query("""
              SELECT DISTINCT p FROM Project p
              LEFT JOIN p.projectMembers pm
              WHERE p.creator.userId = :userId OR pm.user.userId = :userId
              ORDER BY p.creationDate ASC
            """)
    List<Project> findProjectsAccessibleByUserOrderByCreationDateAsc(@Param("userId") Long userId);

    // cerca i progetti per una specifico parola(ignorando maiusc/minusc)

    Page<Project> findByProjectNameContainingIgnoreCase(String projectName, Pageable pageable);


    List<Project> findByProjectNameContainingIgnoreCase(String projectName);

    // metodo paginato con ordinamento per creationDate ascendente

    Page<Project> findAllByOrderByCreationDateAsc(Pageable pageable);

    // cerca i progetti che scadono prima di una specifica data;

    List<Project> findByExpiryDateBefore(LocalDate expiryDate);

    // cerca i progetti che scadono dopo una specifica data;

    List<Project> findByExpiryDateAfter(LocalDate expiryDate);

    // conta quanti progetti ha uno specifico user

    Long countByCreator_UserId(Long userId);
}