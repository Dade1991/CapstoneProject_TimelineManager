package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.Category;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Recupera una singola categoria per ID

    Optional<Category> findById(Long categoryId);

    // cerca una determinata Category per nome

    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    // verifica l'esistenza di una determinata Category (due per due casi differenti)

    boolean existsByProjectAndCategoryNameIgnoreCase(Project project, String categoryName);

    Optional<Category> findByProjectAndCategoryNameIgnoreCase(Project project, String categoryName);

    // restituisce tutte le categorie di un progetto

    List<Category> findByProject_ProjectId(Long projectId);

    // restituisce tutte le categorie di un progetto

    Optional<Category> findByCategoryIdAndProject_ProjectId(Long categoryId, Long projectId);
}