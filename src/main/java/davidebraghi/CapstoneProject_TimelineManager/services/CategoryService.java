package davidebraghi.CapstoneProject_TimelineManager.services;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses.CategoryCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses.CategoryResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses.CategoryUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.Category;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import davidebraghi.CapstoneProject_TimelineManager.entities.Task;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.BadRequestException;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.NotFoundException;
import davidebraghi.CapstoneProject_TimelineManager.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProjectService projectService;

    // FIND_BY_ID_AND_UPDATE

    public CategoryResponse findCategoryByIdAndUpdate(Long categoryId, CategoryUpdateRequest payload) {

        Category foundCategory = findCategoryById(categoryId);

        if (payload.categoryName() != null && !payload.categoryName().isBlank()) {
            foundCategory.setCategoryName(payload.categoryName());
        }
        if (payload.categoryColor() != null && !payload.categoryColor().isBlank()) {
            foundCategory.setCategoryColor(payload.categoryColor());
        }
        Category savedCategory = categoryRepository.save(foundCategory);
        return CategoryResponse.fromEntity(savedCategory);
    }

    // FIND_CATEGORIES_BY_PROJECT_ID

    public List<CategoryResponse> findCategoriesByProjectId(Long projectId) {
        return categoryRepository.findByProject_ProjectId(projectId)
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

        Category saved = categoryRepository.save(category);
        return CategoryResponse.fromEntity(saved);
    }

    // FIND_BY_ID

    public Category findCategoryById(Long categoryId) {
        return categoryRepository.
                findById(categoryId).
                orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " has not been found."));
    }

    // FIND_BY_ID (CategoryResponse)

    public CategoryResponse findCategoryResponseById(Long categoryId) {
        Category category = findCategoryById(categoryId);
        return CategoryResponse.fromEntity(category);
    }

    // FIND_BY_ID_AND_DELETE

    public void findCategoryByIdAndDelete(Long categoryId) {

        Category foundCategory = findCategoryById(categoryId);
        for (Task task : foundCategory.getTasks()) {
            task.getCategories().remove(foundCategory);
        }

        foundCategory.getTasks().clear();

        this.categoryRepository.delete(foundCategory);
    }
}