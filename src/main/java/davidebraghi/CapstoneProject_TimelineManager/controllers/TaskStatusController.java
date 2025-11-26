package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskResponse;
import davidebraghi.CapstoneProject_TimelineManager.entities.Task;
import davidebraghi.CapstoneProject_TimelineManager.services.ProjectMemberService;
import davidebraghi.CapstoneProject_TimelineManager.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks/{taskId}")
public class TaskStatusController {

    @Autowired
    TaskService taskService;
    @Autowired
    ProjectMemberService projectMemberService;

    // POST - COMPLETED TASK - http://localhost:3001/api/projects/{projectId}/tasks/{taskId}/complete

    @PostMapping("/complete")
    @PreAuthorize("@projectMemberService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public TaskResponse completeTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        Task completedTask = taskService.completeTask(projectId, taskId);
        return TaskResponse.fromEntity(completedTask);
    }

    // PATCH - REOPEN COMPLETED TASK - http://localhost:3001/api/projects/{projectId}/tasks/{taskId}/reopening

    @PatchMapping("/reopening")
    @PreAuthorize("@projectMemberService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public TaskResponse reopenTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId
    ) {
        Task reopeningTask = taskService.reopenCompletedTask(projectId, taskId);
        return TaskResponse.fromEntity(reopeningTask);
    }

    // ---------------- CAMBIO STATUS TASK DEDICATO ----------------

    // PATCH - REOPEN COMPLETED TASK - http://localhost:3001/api/projects/{projectId}/tasks/{taskId}/statusUpdate


//    @PutMapping("/{taskId}/status")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public TaskResponse findTaskByIdAndUpdateTaskStatus(
//            @PathVariable Long projectId,
//            @PathVariable Long taskId,
//            @RequestBody Map<String, Long> statusPayload
//    ) {
//        Long newStatusId = statusPayload.get("statusId");
//        Task updatedTask = taskService.findTaskByIdAndProjectAndUpdateTaskStatus(projectId, taskId, newStatusId);
//        return TaskResponse.fromEntity(updatedTask);
//    }

    // PATCH - UPDATE TASK STATUS - http://localhost:3001/api/projects/{projectId}/categories/{categoryId}/tasks/{taskId}/status/{statusId}

    @PatchMapping("/statusUpdate/{statusId}")
    @PreAuthorize("@projectMemberService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public TaskResponse updateTaskStatus(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long statusId
    ) {
        Task task = taskService.findTaskByIdAndProjectAndUpdateTaskStatus(projectId, taskId, statusId);
        return TaskResponse.fromEntity(task);
    }
}
