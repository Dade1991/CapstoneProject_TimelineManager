package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.Task;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.ValidationException;
import davidebraghi.CapstoneProject_TimelineManager.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public Task getTaskById(
            @PathVariable Long taskId
    ) {
        return taskService.findTaskById(taskId);
    }

    // POST - SAVE - http://localhost:3001/api/tasks

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(
            @RequestBody @Validated TaskCreateRequest payload,
            BindingResult validationResult,
            @AuthenticationPrincipal User creator
    ) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().
                    stream().
                    map(fieldError -> fieldError.getDefaultMessage()).
                    toList());
        }
        return taskService.createTask(payload, creator.getUserId());
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
            throw new ValidationException(validationResult.getFieldErrors().
                    stream().
                    map(fieldError -> fieldError.getDefaultMessage()).
                    toList());
        }
        Task updatedTask = taskService.findTaskByIdAndUpdate(taskId, payload);
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
    public List<Task> getAllTasksByProject(
            @PathVariable Long projectId
    ) {
        return taskService.findTaskByProject(projectId);
    }

    // GET - ricerca i tasks per progetto - http://localhost:3001/api/tasks/status/{statusId}

    @GetMapping("/status/{statusId}")
    public List<Task> getAllTasksByStatus(
            @PathVariable Long statusId
    ) {
        return taskService.findTaskByStatus(statusId);
    }

    // GET - ricerca i tasks per progetto - http://localhost:3001/api/tasks/assignee/{userId}

    @GetMapping("/assignee/{userId}")
    public List<Task> getAllTasksByAssignee(
            @PathVariable Long userId
    ) {
        return taskService.findTaskByAssignee(userId);
    }


    // POST - ASSIGN USER TO TASK - http://localhost:3001/api/tasks/{taskId}/assignees/{userId}

    @PostMapping("/{taskId}/assignees/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Task assignUserToTask(
            @PathVariable Long taskId,
            @PathVariable Long userId
    ) {
        return taskService.assignUserToTask(taskId, userId);
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
    public Task completeTask(
            @PathVariable Long taskId
    ) {
        return taskService.completeTask(taskId);
    }

    // PUT - REOPEN COMPLETED TASK - http://localhost:3001/api/tasks/{taskId}/reopen

    @PutMapping("/{taskId}/reopen")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Task reopenCompleteTask(
            @PathVariable Long taskId
    ) {
        return taskService.reopenCompletedTask(taskId);
    }
}