package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Category_DTO_RequestsAndResponses;

import davidebraghi.CapstoneProject_TimelineManager.entities.Category;

public record CategoryResponse(
        Long categoryId,
        String categoryName,
        String categoryColor,
        int position
) {

    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getCategoryColor(),
                category.getPosition()
        );
    }
}