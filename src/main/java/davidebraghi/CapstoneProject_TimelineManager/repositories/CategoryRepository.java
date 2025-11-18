package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // cerca una determinata Category per nome

    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    // verifica l'esistenza di una determinata Category

    boolean existsByCategoryNameIgnoreCase(String categoryName);

    // cerca un determinato progetto per il suo Id

    List<Category> findByProject_ProjectId(Long projectId);
}