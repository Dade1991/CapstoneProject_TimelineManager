package davidebraghi.CapstoneProject_TimelineManager.services;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses.CategoryCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses.CategoryResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses.CategoryUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.Category;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.BadRequestException;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.NotFoundException;
import davidebraghi.CapstoneProject_TimelineManager.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProjectService projectService;

    // FIND_BY_ID_AND_UPDATE

    public CategoryResponse findCategoryByIdAndUpdate(Long projectId, Long categoryId, CategoryUpdateRequest payload) {

        Category foundCategory = findCategoryByIdAndProject(categoryId, projectId);

        boolean dataChanged = false;

        if (payload.categoryName() != null && !payload.categoryName().isBlank()) {
            foundCategory.setCategoryName(payload.categoryName());
        }
        if (payload.categoryColor() != null && !payload.categoryColor().isBlank()) {
            foundCategory.setCategoryColor(payload.categoryColor());
        }
        if (dataChanged && foundCategory.isDefaultInitial()) {
            foundCategory.markAsNoLongerDefault();
            log.info("Category ID {} marked no more as default category.", foundCategory.getCategoryId());
        }
        Category savedCategory = categoryRepository.save(foundCategory);
        return CategoryResponse.fromEntity(savedCategory);
    }

    public List<CategoryResponse> findCategoriesByProjectIdOrdered(Long projectId) {
        return categoryRepository.findByProject_ProjectIdOrderByPositionAsc(projectId)
                .stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // FIND_ALL

    public List<CategoryResponse> findAllCategories() {
        return categoryRepository.findAll().
                stream().
                map(CategoryResponse::fromEntity).
                collect(Collectors.toList());
    }

    // SAVE

    public CategoryResponse createCategory(CategoryCreateRequest payload) {
        Project project = projectService.findProjectById(payload.projectId());

        boolean isDefaultCategoryExists = categoryRepository.existsByProjectAndCategoryNameIgnoreCase(project, payload.categoryName());

        if (isDefaultCategoryExists) {
            throw new BadRequestException("Category " + payload.categoryName() + " already exists.");
        }

        Category category = new Category();
        category.setCategoryName(payload.categoryName());
        category.setCategoryColor(payload.categoryColor() != null ? payload.categoryColor() : "#000000");
        category.setProject(project);
        category.setDefaultInitial(false);

        int position = categoryRepository.findByProject_ProjectId(project.getProjectId()).size();
        category.setPosition(position);

        Category saved = categoryRepository.save(category);
        return CategoryResponse.fromEntity(saved);
    }

    // FIND_BY_ID

    public Category findCategoryByIdAndProject(Long categoryId, Long projectId) {
        return categoryRepository.findByCategoryIdAndProject_ProjectId(categoryId, projectId)
                .orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " has not been found in Project " + projectId));
    }

    // FIND_BY_ID (CategoryResponse)

    public CategoryResponse findCategoryResponseById(Long projectId, Long categoryId) {
        Category category = findCategoryByIdAndProject(categoryId, projectId);
        return CategoryResponse.fromEntity(category);
    }

    // FIND_BY_ID_AND_DELETE

    public void findCategoryByIdAndDelete(Long projectId, Long categoryId) {
        Category foundCategory = findCategoryByIdAndProject(categoryId, projectId);
        foundCategory.getTasks().forEach(task -> task.getCategories().remove(foundCategory));
        foundCategory.getTasks().clear();

        categoryRepository.delete(foundCategory);
        log.info("Category with ID " + categoryId + " deleted from Project " + projectId);
    }

    //    -------- HELPER --------

    //    restitusci categoria per Id

    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " has not been found."));
    }

    //    aggiorna le posizioni in una lista di categorie (ordine definito dal frontEnd)

    public void updateCategoryOrder(Long projectId, List<Long> orderedCategoryIds) {
        AtomicInteger index = new AtomicInteger(0);
        orderedCategoryIds.forEach(categoryId -> {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category not found with id " + categoryId));
            category.setPosition(index.getAndIncrement());
            categoryRepository.save(category);
        });
    }

    //    dopo la cancellazione di una category, riallinea l'array delle categorie per non avere buchi

    public void realignCategoryPositions(Long projectId) {
        List<Category> categories = categoryRepository.findByProject_ProjectIdOrderByPositionAsc(projectId);
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            category.setPosition(i);
            categoryRepository.save(category);
        }
    }
}