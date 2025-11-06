package davidebraghi.CapstoneProject_TimelineManager.services;


import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.*;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.BadRequestException;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.NotFoundException;
import davidebraghi.CapstoneProject_TimelineManager.repositories.ProjectRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.TaskRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.Task_AssigneeRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.Task_StatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private Task_AssigneeRepository task_assigneeRepository;
    @Autowired
    private Task_StatusRepository task_statusRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserService userService;

    // FIND_ALL (paginato)

    public Page<Task> findAllTask(int pageNumber,
                                  int pageSize,
                                  String sortBy) {
        if (pageSize > 50) pageSize = 50;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        return this.taskRepository.findAll(pageable);
    }

    // FIND_ALL (non-paginato)

    public List<Task> findAllTaskWithNoPagination() {
        return this.taskRepository.findAll();
    }

    // SAVE

    public Task createTask(TaskCreateRequest payload, Long creatorId) {

        User foundCreator = userService.findUserById(creatorId);

        Project foundProject = projectRepository.findById(payload.projectId())
                .orElseThrow(() -> new NotFoundException("Project not found."));

        Task_Status foundStatus = task_statusRepository.findById(payload.statusId())
                .orElseThrow(() -> new NotFoundException("Status not found."));

        Task task = new Task(
                payload.taskTitle(),
                payload.taskDescription(),
                payload.taskPriority(),
                payload.taskExpiryDate()
        );
        task.setCreator(foundCreator);
        task.setProject(foundProject);
        task.setStatus(foundStatus);

        Task savedTask = taskRepository.save(task);

        return savedTask;
    }

    // FIND_BY_ID

    public Task findTaskById(Long taskId) {
        return this.taskRepository.
                findById(taskId).
                orElseThrow(() -> new NotFoundException("Task with ID " + taskId + " has not been found."));
    }

    // FIND_TASK_BY_PROJECT

    public List<Task> findTaskByProject(Long projectId) {
        return this.taskRepository.
                findByProject_ProjectId(projectId);
    }

    // FIND_BY_STATUS

    public List<Task> findTaskByStatus(Long statusId) {
        return this.taskRepository.findByStatus_StatusId(statusId);
    }

    // FIND_BY_ASSIGNEE

    public List<Task> findTaskByAssignee(Long userId) {
        return this.task_assigneeRepository.
                findByUser_UserId(userId).
                stream().
                map(Task_Assignee::getTask).
                toList();
    }

    // FIND_BY_ID_AND_UPDATE

    public Task findTaskByIdAndUpdate(Long taskId, TaskUpdateRequest payload) {

        Task foundTask = findTaskById(taskId);

        if (payload.taskTitle() != null && !payload.taskTitle().isBlank()) {
            foundTask.setTaskTitle(payload.taskTitle());
        }
        if (payload.taskDescription() != null) {
            foundTask.setTaskDescription(payload.taskDescription());
        }
        if (payload.taskPriority() != null) {
            foundTask.setTaskPriority(payload.taskPriority());
        }
        if (payload.statusId() != null) {
            Task_Status foundStatus = task_statusRepository.findById(payload.statusId()).
                    orElseThrow(() -> new NotFoundException("Status with ID " + payload.statusId() + " has not been found."));
            foundTask.setStatus(foundStatus);
        }
        if (payload.taskExpiryDate() != null) {
            foundTask.setTaskExpiryDate(payload.taskExpiryDate());
        }

        return this.taskRepository.save(foundTask);
    }

    // FIND_BY_ID_AND_DELETE

    public void deleteTask(Long taskId) {

        Task foundTask = findTaskById(taskId);

        this.taskRepository.delete(foundTask);
    }

    // ASSIGN USER TO TASK

    public void assignUserToTask(Long taskId, Long userId) {

        Task foundTask = findTaskById(taskId);

        User foundUser = userService.findUserById(userId);

        if (task_assigneeRepository.findByTask_TaskIdAndUser_UserId(taskId, userId).isPresent()) {
            throw new BadRequestException("User with ID " + userId + " has been already assigned to this task.");
        }

        Task_Assignee assignee = new Task_Assignee();
        assignee.setTask(foundTask);
        assignee.setUser(foundUser);

        task_assigneeRepository.save(assignee);
    }

    // REMOVE USER FROM TASK

    public void removeUserFromTask(Long taskId, Long userId) {

        Task_Assignee foundTask_Assignee = task_assigneeRepository.findByTask_TaskIdAndUser_UserId(taskId, userId).
                orElseThrow(() -> new NotFoundException("Assignment not found."));

        task_assigneeRepository.delete(foundTask_Assignee);
    }

    // COMPLETE TASK

    public Task completeTask(Long taskId) {

        Task foundTask = findTaskById(taskId);

        if (foundTask.isCompleted()) {
            throw new BadRequestException("Task already completed.");
        }

        foundTask.setCompletedAt(LocalDate.now());
        return taskRepository.save(foundTask);
    }

    // REOPEN COMPLETED TASK

    public Task reopenCompletedTask(Long taskId) {

        Task foundTask = findTaskById(taskId);

        if (!foundTask.isCompleted()) {
            throw new BadRequestException("Task is not completed.");
        }

        foundTask.setCompletedAt(null);
        return taskRepository.save(foundTask);
    }
}