package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses.CategoryCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses.CategoryResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses.CategoryUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // GET - FIND_ALL - http://localhost:3001/api/projects/{projectId}/categories

    @GetMapping
    public List<CategoryResponse> getAllCategories(
            @PathVariable Long projectId
    ) {
        return categoryService.findCategoriesByProjectIdOrdered(projectId);
    }

    // GET - FIND_BY_ID - http://localhost:3001/api/projects/{projectId}/categories/{categoryId}

    @GetMapping("/{categoryId}")
    public CategoryResponse getCategoryById(
            @PathVariable Long projectId,
            @PathVariable Long categoryId
    ) {
        return categoryService.findCategoryResponseById(projectId, categoryId);
    }

    // POST - SAVE - http://localhost:3001/api/projects/{projectId}/categories

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @PathVariable Long projectId,
            @RequestBody @Validated CategoryCreateRequest payload
    ) {
        if (payload.projectId() == null || !payload.projectId().equals(projectId)) {
            throw new IllegalArgumentException("Project ID in payload must match @PathVariable");
        }
        return categoryService.createCategory(payload);
    }

    // PUT - FIND_BY_ID_AND_UPDATE - http://localhost:3001/api/projects/{projectId}/categories{categoryId}

    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CategoryResponse getCategoryByIdAndUpdate(
            @PathVariable Long projectId,
            @PathVariable Long categoryId,
            @RequestBody @Validated CategoryUpdateRequest payload
    ) {
        return categoryService.findCategoryByIdAndUpdate(projectId, categoryId, payload);
    }

    // DELETE - FIND_BY_ID_AND_DELETE - http://localhost:3001/api/projects/{projectId}/categories{categoryId}

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable Long projectId,
            @PathVariable Long categoryId
    ) {
        categoryService.findCategoryByIdAndDelete(projectId, categoryId);
        categoryService.realignCategoryPositions(projectId);
    }
}