package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.Task;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.enums.TaskPriorityENUM;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.ValidationException;
import davidebraghi.CapstoneProject_TimelineManager.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // GET - FIND_ALL (paginato) - http://localhost:3001/api/tasks

    @GetMapping
    public Page<Task> getAllTasks(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "taskId") String sortBy
    ) {
        return taskService.findAllTask(pageNumber, pageSize, sortBy);
    }

    // GET - FIND_BY_ID - http://localhost:3001/api/tasks/{taskId}

    @GetMapping("/{taskId}")
    public TaskResponse getTaskById(
            @PathVariable Long taskId
    ) {
        return TaskResponse.fromEntity(taskService.findTaskById(taskId));
    }

    // POST - SAVE - http://localhost:3001/api/tasks

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @RequestBody @Validated TaskCreateRequest payload,
            BindingResult validationResult,
            @AuthenticationPrincipal User creator
    ) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(
                    validationResult.getFieldErrors()
                            .stream()
                            .map(fieldError -> fieldError.getDefaultMessage())
                            .toList()
            );
        }
        Task createdTask = taskService.createTask(payload, creator.getUserId());
        return TaskResponse.fromEntity(createdTask);
    }

    // PUT - FIND_BY_ID_AND_UPDATE - http://localhost:3001/api/tasks/{taskId}

    @PutMapping("/{taskId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TaskResponse getTaskByIdAndUpdate(
            @PathVariable Long taskId,
            @RequestBody @Validated TaskUpdateRequest payload,
            BindingResult validationResult,
            @AuthenticationPrincipal User currentUser
    ) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList());
        }
        var updatedTask = taskService.findTaskByIdAndUpdate(taskId, payload);
        updatedTask.setLastModifiedBy(currentUser);
        updatedTask = taskService.saveTaskChanges(updatedTask);
        return TaskResponse.fromEntity(updatedTask);
    }

    // DELETE - FIND_BY_ID_AND_DELETE - http://localhost:3001/api/tasks/{taskId}

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @PathVariable Long taskId
    ) {
        taskService.findTaskByIdAndDelete(taskId);
    }

    // ---------------- VISUALIZZAZIONE GENERALE TASKS ----------------

    // GET - ricerca i tasks per progetto - http://localhost:3001/api/tasks/project/{projectId}

    @GetMapping("/project/{projectId}")
    public List<TaskResponse> getAllTasksByProject(
            @PathVariable Long projectId
    ) {
        return taskService.findTaskByProject(projectId).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    // GET - ricerca i tasks per progetto - http://localhost:3001/api/tasks/status/{statusId}

    @GetMapping("/status/{statusId}")
    public List<TaskResponse> getAllTasksByStatus(
            @PathVariable Long statusId
    ) {
        return taskService.findTaskByStatus(statusId).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }
    // GET - ricerca i tasks per progetto - http://localhost:3001/api/tasks/assignee/{userId}

    @GetMapping("/assignee/{userId}")
    public List<TaskResponse> getAllTasksByAssignee(
            @PathVariable Long userId
    ) {
        return taskService.findTaskByAssignee(userId).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    // POST - ASSIGN USER TO TASK - http://localhost:3001/api/tasks/{taskId}/assignees/{userId}

    @PostMapping("/{taskId}/assignees/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse assignUserToTask(
            @PathVariable Long taskId,
            @PathVariable Long userId
    ) {
        var assignedTask = taskService.assignUserToTask(taskId, userId);
        return TaskResponse.fromEntity(assignedTask);
    }

    // DELETE - REMOVE USER FROM TASK - http://localhost:3001/api/tasks/{taskId}/assignees/{userId}

    @DeleteMapping("/{taskId}/assignees/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserFromTask(
            @PathVariable Long taskId,
            @PathVariable Long userId
    ) {
        taskService.removeUserFromTask(taskId, userId);
    }

    // ---------------- COMPLETA/RIAPRI TASK ----------------

    // PUT - COMPLETE TASK - http://localhost:3001/api/tasks/{taskId}/complete

    @PutMapping("/{taskId}/complete")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TaskResponse completeTask(
            @PathVariable Long taskId
    ) {
        var task = taskService.completeTask(taskId);
        return TaskResponse.fromEntity(task);
    }

    // PUT - REOPEN COMPLETED TASK - http://localhost:3001/api/tasks/{taskId}/reopen

    @PutMapping("/{taskId}/reopen")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TaskResponse reopenTask(
            @PathVariable Long taskId
    ) {
        var task = taskService.reopenCompletedTask(taskId);
        return TaskResponse.fromEntity(task);
    }

    // ---------------- FILTRI CUSTOM PER TASK ----------------

    // GET - http://localhost:3001/api/tasks/search?parametri

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Page<TaskResponse> searchTasks(

            // parametri che si aggiungeranno con @RequestParam alla stringa URL dopo "search" coem parte del path

            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long statusId,
            @RequestParam(required = false) TaskPriorityENUM taskPriority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Boolean isCompleted,
            @RequestParam(required = false) Boolean isOverdue,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate createdAfter,
            @RequestParam(required = false) LocalDate createdBefore,
            @RequestParam(required = false) LocalDate expiryDateBefore,
            @RequestParam(required = false) LocalDate expiryDateAfter,
            @RequestParam(required = false) Long excludeStatusId,
            @RequestParam(required = false) TaskPriorityENUM excludePriority,
            @RequestParam(required = false) String createdWithinLast,
            @RequestParam(required = false) String expiringIn,
            @RequestParam(required = false) Boolean createdThisWeek,
            @RequestParam(required = false) Boolean createdThisMonth,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "taskId") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {

        Page<Task> tasksPage = taskService.findTasksWithSmartFilters(
                projectId,
                statusId,
                taskPriority,
                assigneeId,
                isCompleted,
                isOverdue,
                search,
                createdAfter,
                createdBefore,
                expiryDateBefore,
                expiryDateAfter,
                excludeStatusId,
                excludePriority,
                createdWithinLast,
                expiringIn,
                createdThisWeek,
                createdThisMonth,
                categoryIds,
                pageNumber,
                pageSize,
                sortBy,
                sortDirection
        );

        return tasksPage.map(TaskResponse::fromEntity);
    }
}