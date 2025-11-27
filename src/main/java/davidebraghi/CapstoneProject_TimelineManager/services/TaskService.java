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
import java.util.concurrent.atomic.AtomicInteger;
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

    // SAVE

    public Task createTask(Long projectId, TaskCreateRequest payload, Long creatorId) {
        User creator = userService.findUserById(creatorId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found."));

        Set<Category> categories;

        if (payload.categoryIds() == null || payload.categoryIds().isEmpty()) {
            // *** Se non sono passate categorie, assegna automaticamente la categoria Default ***
            Category defaultCategory = categoryRepository.findByProjectAndCategoryNameIgnoreCase(project, "Default")
                    .orElseThrow(() -> new NotFoundException("Default category not found for project"));
            categories = Set.of(defaultCategory);
        } else {
            categories = fetchAndValidateCategories(payload.categoryIds(), projectId);
        }

        Task task = new Task(
                payload.taskTitle(),
                payload.taskDescription(),
                payload.taskPriority(),
                payload.taskExpiryDate()
        );

        task.setCreator(creator);
        task.setProject(project);
        task.setCategories(categories);

        if (payload.position() != null) {
            task.setPosition(payload.position());
        } else {
            Integer maxPosition = taskRepository.findMaxPositionByProjectId(projectId);
            task.setPosition(maxPosition == null ? 1 : maxPosition + 1);
        }

        return taskRepository.save(task);
    }

//     FIND TASK BY PROJECT, CATEGORY & TASK ID

    public Task findTaskByProjectCategoryAndTaskId(Long projectId, Long categoryId, Long taskId) {
        Task task = taskRepository.findByProject_ProjectIdAndTaskId(projectId, taskId)
                .orElseThrow(() -> new NotFoundException(
                        "Task with ID " + taskId + " has not been found in Project " + projectId));

        boolean inCategory = task.getCategories().stream()
                .anyMatch(cat -> cat.getCategoryId().equals(categoryId));

        if (!inCategory) {
            throw new BadRequestException(
                    "Task with ID " + taskId + " does not belong to Category " + categoryId);
        }

        return task;
    }

//    public Task findTaskByProjectCategoryAndTaskId(Long projectId, Long categoryId, Long taskId) {
//        log.debug("Chiamato findTaskByProjectCategoryAndTaskId con projectId={}, categoryId={}, taskId={}", projectId, categoryId, taskId);
//
//        Task task = taskRepository.findByProject_ProjectIdAndTaskId(projectId, taskId)
//                .orElseThrow(() -> new NotFoundException(
//                        "Task with ID " + taskId + " has not been found in Project " + projectId));
//
//        List<Long> categoryIds = task.getCategories().stream()
//                .map(Category::getCategoryId)
//                .toList();
//        log.debug("Categorie associate al task {}: {}", taskId, categoryIds);
//
//        boolean inCategory = categoryIds.contains(categoryId);
//        log.debug("Il task appartiene alla categoria? {}", inCategory);
//
//        if (!inCategory) {
//            throw new BadRequestException(
//                    "Task with ID " + taskId + " does not belong to Category " + categoryId);
//        }
//
//        return task;
//    }

    // UPDATE TASK CATEGORY

    public Task updateTaskCategories(Long projectId, List<Long> categoryIds, Long taskId) {
        // Recupera la task in base a projectId e taskId
        Task task = taskRepository.findByProject_ProjectIdAndTaskId(projectId, taskId)
                .orElseThrow(() -> new NotFoundException("Task not found for project " + projectId + " and task " + taskId));

        // Recupera le categorie in base agli ID forniti
        Set<Category> categories = categoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toSet());

        // Aggiorna le categorie assegnate alla task
        task.setCategories(categories);

        // Salva la task con le categorie aggiornate
        return taskRepository.save(task);
    }

    // FIND_BY_ID_AND_UPDATE

    public Task updateTask(Long projectId, Long categoryId, Long taskId, TaskUpdateRequest payload) {
        Task task = findTaskByProjectCategoryAndTaskId(projectId, categoryId, taskId);

        if (payload.taskTitle() != null && !payload.taskTitle().isBlank()) {
            task.setTaskTitle(payload.taskTitle());
        }

        if (payload.taskDescription() != null) {
            task.setTaskDescription(payload.taskDescription());
        }

        if (payload.taskPriority() != null) {
            task.setTaskPriority(payload.taskPriority());
        }

        if (payload.taskExpiryDate() != null) {
            task.setTaskExpiryDate(payload.taskExpiryDate());
        }

        if (payload.categoryIds() != null) {
            Set<Category> categories = fetchAndValidateCategories(payload.categoryIds(), projectId);
            task.getCategories().clear();
            task.getCategories().addAll(categories);
        }

        if (payload.position() != null) {
            task.setPosition(payload.position());
        }

        // gestione di isCompleted e completedAt per update status

        Boolean isCompletedPayload = payload.isCompleted();
        LocalDate completedAtPayload = payload.completedAt();

        if (isCompletedPayload != null) {
            task.setIsCompleted(isCompletedPayload);
            if (isCompletedPayload) {

                // se completata e completedAt non valorizzata, imposta a oggi

                if (completedAtPayload == null) {
                    task.setCompletedAt(LocalDate.now());
                } else {
                    task.setCompletedAt(completedAtPayload);
                }
            } else {

                // se non completata, azzera completedAt

                task.setCompletedAt(null);
            }
        } else if (completedAtPayload != null) {

            // se isCompleted non è presente, ma è presente completedAt, aggiorna isCompleted

            task.setCompletedAt(completedAtPayload);
            task.setIsCompleted(true);
        }

        return taskRepository.save(task);
    }

    // FIND_BY_ID_AND_DELETE

    public void deleteTask(Long projectId, Long categoryId, Long taskId) {
        Task task = findTaskByProjectCategoryAndTaskId(projectId, categoryId, taskId);
        taskRepository.delete(task);
    }

    // FIND TASK BY PROJECT ID AND USER ID

    public List<Task> findTasksByProjectAndUser(Long projectId, Long userId) {
        return taskRepository.findTasksByProjectAndUser(projectId, userId);
    }

    // FIND TASK BY PROJECT AND CATEGORY ID

    public List<Task> findTaskByProjectCategory(Long projectId, Long categoryId) {
        return taskRepository.findByCategories_CategoryIdOrderByPositionAsc(categoryId);
    }

    // COMPLETE TASK

//    public Task completeTask(Long projectId, Long categoryId, Long taskId) {
//        Task task = findTaskByProjectCategoryAndTaskId(projectId, categoryId, taskId);
//        if (task.isCompleted()) {
//            throw new BadRequestException("Task already completed.");
//        }
//        task.setCompletedAt(LocalDate.now());
//        task.setIsCompleted(true);
//        return taskRepository.save(task);
//    }
//
    // COMPLETE TASK

    public Task completeTask(Long projectId, Long taskId) {
        Task task = findTaskByProjectIdAndTaskId(projectId, taskId);
        if (task.isCompleted()) {
            throw new BadRequestException("Task already completed.");
        }
        task.setCompletedAt(LocalDate.now());
        task.setIsCompleted(true);
        return taskRepository.save(task);
    }

    // REOPEN COMPLETED TASK

    public Task reopenCompletedTask(Long projectId, Long taskId) {
        Task task = findTaskByProjectIdAndTaskId(projectId, taskId);
        if (!task.isCompleted()) {
            throw new BadRequestException("Task is not completed.");
        }
        task.setCompletedAt(null);
        task.setIsCompleted(false);
        return taskRepository.save(task);
    }

    //    ======== STATUS ========

    // FIND TASK BY ID AND CREATE/UPDATE STATUS

    public Task findTaskByIdAndProjectAndUpdateTaskStatus(Long projectId, Long taskId, Long taskStatusId) {
        Task foundTask = taskRepository.findByProject_ProjectIdAndTaskId(projectId, taskId)
                .orElseThrow(() -> new NotFoundException("Task not found for project " + projectId + " and task " + taskId));

        Task_Status status = task_statusRepository.findByTaskStatusId(taskStatusId)
                .orElseThrow(() -> new NotFoundException("Status not found: " + taskStatusId));

        foundTask.setStatus(status);

        return taskRepository.save(foundTask);
    }

    //    ======== METODI UTLITY ========

    // FIND_ALL ordinato per categoria

    public List<Task> findTasksByCategoryOrdered(Long categoryId) {
        return taskRepository.findByCategories_CategoryIdOrderByPositionAsc(categoryId);
    }

    // FIND_ALL (non-paginato)

    public List<Task> findAllTaskByProjectId(Long projectId) {
        return this.taskRepository.findByProject_ProjectId(projectId);
    }

    // FIND_TASK_BY_PROJECT

    public List<Task> findTaskByProject(Long projectId) {
        return this.taskRepository.
                findByProjectIdWithCategories(projectId);
    }

    // FIND_BY_STATUS

    public List<Task> findTaskByProjectAndStatus(Long projectId, Long statusId) {
        return this.taskRepository.findByProject_ProjectIdAndStatus_TaskStatusId(projectId, statusId);
    }

    // FIND_BY_ASSIGNEE

    public List<Task> findTaskByProjectAndAssignee(Long projectId, Long userId) {
        return task_assigneeRepository.findByTask_TaskIdAndUser_UserId(projectId, userId).
                stream().
                map(Task_Assignee::getTask).
                toList();
    }

    // ======== FIND_WITH_SMART_FILTERS ========

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

    // ===========================================================

    //    -------- HELPER--------

    public Task saveTaskChanges(Task task) {
        return taskRepository.save(task);
    }

    private void validateCategoriesBelongToProject(Set<Category> categories, Long projectId) {
        for (Category category : categories) {
            if (!category.getProject().getProjectId().equals(projectId)) {
                throw new BadRequestException(
                        "Category ID " + category.getCategoryId() + " does not belong to Project " + projectId);
            }
        }
    }

    private Set<Category> fetchAndValidateCategories(Set<Long> categoryIds, Long projectId) {
        if (categoryIds == null || categoryIds.isEmpty()) return Set.of();

        Set<Category> categories = categoryIds.stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Category with ID " + id + " not found.")))
                .collect(Collectors.toSet());

        validateCategoriesBelongToProject(categories, projectId);
        return categories;
    }

    public Task findTaskByProjectIdAndTaskId(Long projectId, Long taskId) {
        return taskRepository.findByProject_ProjectIdAndTaskId(projectId, taskId)
                .orElseThrow(() -> new NotFoundException(
                        "Task with ID " + taskId + " not found in Project " + projectId));
    }

    //    aggiorna le posizioni in una lista di tasks (ordine definito dal frontEnd)

    public void updateTaskOrder(Long projectId, Long categoryId, List<Long> orderedTaskIds) {
        AtomicInteger index = new AtomicInteger(0);
        orderedTaskIds.forEach(taskId -> {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new NotFoundException("Task not found with id " + taskId));

            // Controlla che la task appartenga al progetto corretto
            if (!task.getProject().getProjectId().equals(projectId)) {
                throw new BadRequestException("Task " + taskId + " does not belong to Project " + projectId);
            }

            // Facoltativo: controlla che la task appartenga anche alla categoria
            if (categoryId != null) {
                boolean belongsToCategory = task.getCategories().stream()
                        .anyMatch(cat -> cat.getCategoryId().equals(categoryId));
                if (!belongsToCategory) {
                    throw new BadRequestException("Task " + taskId + " does not belong to Category " + categoryId);
                }
            }

            task.setPosition(index.getAndIncrement());
            taskRepository.save(task);
        });
    }

    //    aggiorna le posizioni in una lista di categorie (ordine definito dal frontEnd)

    public void updateTaskCategory(Long projectId, Long taskId, Long newCategoryId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found with id " + projectId));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id " + taskId));
        Category category = categoryRepository.findById(newCategoryId)
                .orElseThrow(() -> new NotFoundException("Category not found with id " + newCategoryId));

        // rimuovi vecchie categorie e imposta solo la nuova categoria

        task.getCategories().clear();
        task.getCategories().add(category);
        taskRepository.save(task);
    }

    //    dopo la cancellazione di una task, riallinea l'array delle task per non avere buchi

    public void realignTaskPositions(Long categoryId) {
        List<Task> tasks = taskRepository.findByCategories_CategoryIdOrderByPositionAsc(categoryId);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            task.setPosition(i);
            taskRepository.save(task);
        }
    }
}

//    ======= WAITING AREA =======

// ASSIGN USER TO TASK

//    public Task assignUserToTask(Long projectId, Long categoryId, Long taskId, Long userId) {
//        Task task = findTaskByProjectCategoryAndTaskId(projectId, categoryId, taskId);
//        User user = userService.findUserById(userId);
//
//        if (task_assigneeRepository.findByTask_TaskIdAndUser_UserId(taskId, userId).isPresent()) {
//            throw new BadRequestException("User already assigned to this task.");
//        }
//
//        Task_Assignee assignment = new Task_Assignee();
//        assignment.setTask(task);
//        assignment.setUser(user);
//        task_assigneeRepository.save(assignment);
//
//        return task;
//    }
//
//     REMOVE USER FROM TASK
//
//    public void removeUserFromTask(Long projectId, Long categoryId, Long taskId, Long userId) {
//        Task_Assignee assignment = task_assigneeRepository.findByTask_TaskIdAndUser_UserId(taskId, userId)
//                .orElseThrow(() -> new NotFoundException("Assignment not found."));
//        task_assigneeRepository.delete(assignment);
//    }

