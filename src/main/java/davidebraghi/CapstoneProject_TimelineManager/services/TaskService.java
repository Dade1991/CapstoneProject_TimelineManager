package davidebraghi.CapstoneProject_TimelineManager.services;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.*;
import davidebraghi.CapstoneProject_TimelineManager.enums.TaskPriorityENUM;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.BadRequestException;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.NotFoundException;
import davidebraghi.CapstoneProject_TimelineManager.repositories.*;
import davidebraghi.CapstoneProject_TimelineManager.repositories.Specifications.TaskSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Autowired
    private CategoryRepository categoryRepository;

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

        if (payload.categoryIds() != null && !payload.categoryIds().isEmpty()) {
            Set<Category> categories = payload.categoryIds().
                    stream().
                    map(id -> categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category with ID " + id + " has not been found."))).
                    collect(Collectors.toSet());
            task.setCategories(categories);
        }

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
        return this.taskRepository.findByStatus_TaskStatusId(statusId);
    }

    // FIND_BY_ASSIGNEE

    public List<Task> findTaskByAssignee(Long userId) {
        return this.task_assigneeRepository.
                findByUser_UserId(userId).
                stream().
                map(Task_Assignee::getTask).
                toList();
    }

    // FIND_WITH_SMART_FILTERS

    public Page<Task> findTasksWithSmartFilters(
            Long projectId,
            Long statusId,
            TaskPriorityENUM taskPriority,
            Long assigneeId,
            Boolean isCompleted,
            Boolean isOverdue,
            String search,
            LocalDate createdAfter,
            LocalDate createdBefore,
            LocalDate expiryDateBefore,
            LocalDate expiryDateAfter,
            Long excludeStatusId,
            TaskPriorityENUM excludePriority,
            String createdWithinLast,
            String expiringIn,
            Boolean createdThisWeek,
            Boolean createdThisMonth,
            List<Long> categoryIds,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirection
    ) {

        // limita la dimensione della pagina a max 50

        if (pageSize > 50) pageSize = 50;

        // crea l'oggetto Sort

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

        // se ci sono CategoryIds, chiama il metodo custom

        if (categoryIds != null && !categoryIds.isEmpty()) {
            return taskRepository.findDistinctByCategories_CategoryIdIn(categoryIds, pageable);
        }

        // crea la Specification con i filtri

        Specification<Task> spec = TaskSpecification.buildSpecification(
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
                createdThisMonth
        );

        // esegui la query con i filtri e la paginazione

        return taskRepository.findAll(spec, pageable);
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
        if (payload.categoryIds() != null) {
            Set<Category> categories = payload.categoryIds().
                    stream().
                    map(id -> categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category with ID " + id + " not found."))).
                    collect(Collectors.toSet());
            foundTask.setCategories(categories);
        }

        return this.taskRepository.save(foundTask);
    }

    public Task saveTaskChanges(Task task) {
        return taskRepository.save(task);
    }

    // FIND_BY_ID_AND_DELETE

    public void findTaskByIdAndDelete(Long taskId) {

        Task foundTask = findTaskById(taskId);

        this.taskRepository.delete(foundTask);
    }

    // ASSIGN USER TO TASK

    public Task assignUserToTask(Long taskId, Long userId) {

        Task foundTask = findTaskById(taskId);

        User foundUser = userService.findUserById(userId);

        if (task_assigneeRepository.findByTask_TaskIdAndUser_UserId(taskId, userId).isPresent()) {
            throw new BadRequestException("User with ID " + userId + " has been already assigned to this task.");
        }

        Task_Assignee assignee = new Task_Assignee();
        assignee.setTask(foundTask);
        assignee.setUser(foundUser);

        task_assigneeRepository.save(assignee);
        return foundTask;
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