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
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // GET - FIND_ALL - http://localhost:3001/api/categories

    @GetMapping
    public List<CategoryResponse> getAllCategories(
            @RequestParam(required = false) Long projectId
    ) {
        if (projectId != null) {
            return categoryService.findCategoriesByProjectId(projectId);
        }
        return categoryService.findAllCategories();
    }
    // GET - FIND_BY_ID - http://localhost:3001/api/categories/{categoriesId}

    @GetMapping("/{categoryId}")
    public CategoryResponse getCategoryById(
            @PathVariable Long categoryId
    ) {
        return categoryService.findCategoryResponseById(categoryId);
    }

    // POST - SAVE - http://localhost:3001/api/categories

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @RequestBody @Validated CategoryCreateRequest payload
    ) {
        return categoryService.createCategory(payload);
    }

    // PUT - FIND_BY_ID_AND_UPDATE - http://localhost:3001/api/categories/{categoryId}

    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CategoryResponse getCategoryByIdAndUpdate(
            @PathVariable Long categoryId,
            @RequestBody @Validated CategoryUpdateRequest payload
    ) {
        return categoryService.findCategoryByIdAndUpdate(categoryId, payload);
    }

    // DELETE - FIND_BY_ID_AND_DELETE - http://localhost:3001/api/categories/{categoryId}

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable Long categoryId
    ) {
        categoryService.findCategoryByIdAndDelete(categoryId);
    }
}