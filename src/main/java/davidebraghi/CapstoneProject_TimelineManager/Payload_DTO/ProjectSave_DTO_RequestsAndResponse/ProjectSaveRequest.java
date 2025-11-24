package davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.ProjectSave_DTO_RequestsAndResponse;

import java.util.List;

public record ProjectSaveRequest(
        List<CategoryOrder> categories
) {

    public record CategoryOrder(
            Long categoryId,
            int position,
            List<TaskOrder> tasks
    ) {
    }

    public record TaskOrder(
            Long taskId,
            int position
    ) {
    }
}
