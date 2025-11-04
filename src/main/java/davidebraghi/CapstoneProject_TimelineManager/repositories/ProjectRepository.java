package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // cerca i progetti per uno specifico creatore(user)

    List<Project> findByCreator_UserId(Long userId);

    // cerca i progetti per una specifico parola(ignorando maiusc/minusc)

    List<Project> findByProjectNameContainingIgnoreCase(String projectName);

    // cerca i progetti che scadono prima di una specifica data;

    List<Project> findByExpiryDateBefore(LocalDate expiryDate);

    // cerca i progetti che scadono dopo una specifica data;

    List<Project> findByExpiryDateAfter(LocalDate expiryDate);

    // conta quanti progetti ha uno specifico user

    Long countByCreator_UserId(Long userId);
}