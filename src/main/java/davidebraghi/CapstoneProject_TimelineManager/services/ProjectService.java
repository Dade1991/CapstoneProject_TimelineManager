package davidebraghi.CapstoneProject_TimelineManager.services;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.ProjectSave_DTO_RequestsAndResponse.ProjectSaveRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.Category;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import davidebraghi.CapstoneProject_TimelineManager.entities.Task;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.NotFoundException;
import davidebraghi.CapstoneProject_TimelineManager.repositories.CategoryRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.ProjectRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProjectMemberService projectMemberService;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          CategoryRepository categoryRepository,
                          UserService userService,
                          ProjectMemberService projectMemberService) {
        this.projectRepository = projectRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.projectMemberService = projectMemberService;
    }

    public boolean isUserCreator(Long projectId, Long userId) {
        return projectMemberService.isUserCreator(projectId, userId);
    }

    public boolean hasPermission(Long projectId, Long userId, ProjectPermissionENUM permission) {
        return projectMemberService.hasPermission(projectId, userId, permission);
    }

    // FIND_ALL (paginato)

    public Page<Project> findAllProject(int pageNumber,
                                        int pageSize,
                                        String sortBy) {
        if (pageSize > 50) pageSize = 50;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).ascending());
        return this.projectRepository.findAll(pageable);
    }

    // FIND_ALL (non-paginato)

    public List<Project> findAllProjectWithNoPagination() {
        return this.projectRepository.findAll();
    }

    // FIND_ALL_PROJECT_IF_MEMBER (projectService)

    public List<Project> findAccessibleProjectsByUser(Long userId) {
        return projectRepository.findProjectsAccessibleByUserOrderByCreationDateAsc(userId);
    }

    // SAVE/CREATE

    public Project createProject(ProjectCreateRequest payload,
                                 Long creatorId) {

        User foundCreator = userService.findUserById(creatorId);

        Project project = new Project(
                payload.projectName(),
                payload.projectDescription(),
                payload.expiryDate()
        );

        project.setCreator(foundCreator);

        Project savedProject = projectRepository.save(project);

        // assegnare il creatore come ADMIN

        projectMemberService.addMemberToProject(savedProject.getProjectId(), creatorId, RoleNameENUM.CREATOR);

        // crea categoria di default per il progetto appena creato

        createDefaultCategoryForProject(savedProject);

        return savedProject;
    }

    // crea una Category di default all'avvio di ogni progetto

    private void createDefaultCategoryForProject(Project project) {
        boolean isCategoryAlreadyExists = categoryRepository.existsByProjectAndCategoryNameIgnoreCase(project, "Default");
        if (isCategoryAlreadyExists) {
            log.info("La categoria 'Default' esiste già per il progetto ID {}",
                    project.getProjectId());
            return;
        }
        Category defaultCategory = new Category();
        defaultCategory.setProject(project);
        defaultCategory.setCategoryName("Default");
        defaultCategory.setCategoryColor("#000000");
        categoryRepository.save(defaultCategory);
    }

    // recupera progetti paginati e ordinati per data creazione

    public Page<ProjectResponse> findAllProjectsPaged(int pageNumber, int pageSize) {
        if (pageSize > 50) pageSize = 50;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("creationDate").ascending());

        return projectRepository.findAll(pageable)
                .map(ProjectResponse::fromEntity);
    }

    // FIND_BY_ID

    public Project findProjectById(Long projectId) {
        return this.projectRepository.
                findById(projectId).
                orElseThrow(() -> new NotFoundException("Project with ID " + projectId + " has not been found."));
    }

    // recupera tutti i progetti ordinati per data creazione (completa lista senza paginazione)

    public List<ProjectResponse> findAllProjectsOrdered() {
        return projectRepository.findAllByOrderByCreationDateAsc()
                .stream()
                .map(ProjectResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // FIND_ALL_PROJECT_BY_CREATOR_USER

    public List<Project> findProjectsByCreatorId(Long creatorId) {
        return this.projectRepository.
                findByCreator_UserId(creatorId);
    }

    // FIND_BY_ID_AND_UPDATE

    public Project findProjectByIdAndUpdate(Long projectId,
                                            ProjectUpdateRequest payload) {

        Project foundProject = findProjectById(projectId);

        if (payload.projectName() != null && !payload.projectName().isBlank()) {
            foundProject.setProjectName(payload.projectName());
        }
        if (payload.projectDescription() != null) {
            foundProject.setProjectDescription(payload.projectDescription());
        }
        if (payload.expiryDate() != null) {
            foundProject.setExpiryDate(payload.expiryDate());
        }

        return this.projectRepository.save(foundProject);
    }

    // FIND_BY_ID_AND_DELETE

    public void findProjectByIdAndDelete(Long projectId) {

        Project foundProject = findProjectById(projectId);

        this.projectRepository.delete(foundProject);
    }

//    ______________________________________

    // FIND_ALL_PROJECT_BY_USER_ID (even if not creators)

    public List<Project> findByCreatorId(Long creatorId) {
        return projectRepository.findByCreator_UserId(creatorId);
    }

//    SAVE WHOLE PROJECT

    public void saveProjectOrder(Long projectId, ProjectSaveRequest saveRequest) {

        // percorre tutte le categorie ricevute

        for (ProjectSaveRequest.CategoryOrder catOrder : saveRequest.categories()) {

            Category category = categoryRepository.findById(catOrder.categoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id " + catOrder.categoryId()));
            category.setPosition(catOrder.position());
            categoryRepository.save(category);

            // percorre tutte le task di questa categoria

            for (ProjectSaveRequest.TaskOrder taskOrder : catOrder.tasks()) {
                Task task = taskRepository.findById(taskOrder.taskId())
                        .orElseThrow(() -> new NotFoundException("Task not found with id " + taskOrder.taskId()));
                task.setPosition(taskOrder.position());
                taskRepository.save(task);

            }
        }
    }
}