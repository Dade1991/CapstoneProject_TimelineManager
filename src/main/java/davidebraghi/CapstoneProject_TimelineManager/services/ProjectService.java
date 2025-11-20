package davidebraghi.CapstoneProject_TimelineManager.services;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.RoleChange_DTO_RequestsAndResponses.RoleChangeResponse;
import davidebraghi.CapstoneProject_TimelineManager.entities.*;
import davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.BadRequestException;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.NotFoundException;
import davidebraghi.CapstoneProject_TimelineManager.repositories.CategoryRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.ProjectRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.Project_User_RoleRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.User_RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private Project_User_RoleRepository project_user_roleRepository;
    @Autowired
    private User_RoleRepository user_roleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryRepository categoryRepository;

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

    // SAVE

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

        // Assegnare il creatore come ADMIN

        assignUserToProject(savedProject.getProjectId(), creatorId, RoleNameENUM.CREATOR);

        // Crea categoria di default per il progetto appena creato

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
        defaultCategory.setCategoryColor("#000000"); // colore di default grigio chiaro
        categoryRepository.save(defaultCategory);
    }

    // FIND_BY_ID

    public Project findProjectById(Long projectId) {
        return this.projectRepository.
                findById(projectId).
                orElseThrow(() -> new NotFoundException("Project with ID " + projectId + " has not been found."));
    }

    // FIND_ALL_PROJECT_BY_CREATOR_USER

    public List<Project> findProjectsByCreatorId(Long creatorId) {
        return this.projectRepository.
                findByCreator_UserId(creatorId);
    }

    // FIND_ALL_PROJECT_BY_USER_ID (even if not creators)

    public List<Project> findProjectsByUserId(Long userId) {
        List<Project_User_Role> relations = project_user_roleRepository.findByUser_UserId(userId);
        return relations.stream().
                map(Project_User_Role::getProject).
                distinct().
                toList();
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

    // ---------------- GESTIONE MEMBRI DEL PROGETTO ----------------

    // controlla che uno user sia anche il creator di quel progetto

    public boolean isUserCreator(Long projectId,
                                 Long userId) {
        Project foundProject = findProjectById(projectId);
        return foundProject != null && foundProject.getCreator().getUserId().equals(userId);
    }

    // controlla che uno user sia anche un membro di quel progetto

    public boolean isUserMember(Long projectId,
                                Long userId) {
        return project_user_roleRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId).isPresent();
    }

    // aggiunge uno user al progetto con un ruolo specifico, senza duplicati

    public void addMemberToProject(Long projectId, Long userId, RoleNameENUM roleName) {
        if (project_user_roleRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId).isPresent()) {
            throw new BadRequestException("The user with ID " + userId + " has already been assigned to this project.");
        }
        Project foundProject = findProjectById(projectId);
        User foundUser = userService.findUserById(userId);
        User_Role userRole = user_roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found."));

        Project_User_Role relation = new Project_User_Role();
        relation.setProject(foundProject);
        relation.setUser(foundUser);
        relation.setRole(userRole);

        project_user_roleRepository.save(relation);
    }

    // rimuovere un membro da un progetto, impedisce di rimuove Creator

    public void removeMemberFromProject(Long projectId,
                                        Long userId) {
        if (isUserCreator(projectId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The Creator cannot be removed from the project.");
        }

        Project_User_Role relation = project_user_roleRepository
                .findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found in the project."));

        project_user_roleRepository.delete(relation);
    }

    // cambiare il ruolo ad uno specifico user

    public RoleChangeResponse changeUserRole(Long projectId, Long currentUserId, Long targetUserId, RoleNameENUM newRole) {
        if (newRole == RoleNameENUM.CREATOR && !hasPermission(projectId, currentUserId, ProjectPermissionENUM.CREATOR_ACTIONS)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the Creator can assign the Creator role.");
        }

        boolean isTargetCreator = isUserCreator(projectId, targetUserId);
        if (isTargetCreator && !currentUserId.equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify the Creator role.");
        }

        Project_User_Role relation = project_user_roleRepository.findByProject_ProjectIdAndUser_UserId(projectId, targetUserId)
                .orElseThrow(() -> new NotFoundException("User with ID " + targetUserId + " not found in the project."));

        User_Role role = user_roleRepository.findByRoleName(newRole)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        relation.setRole(role);
        project_user_roleRepository.save(relation);

        User user = relation.getUser();
        String nickname = user.getNickname();

        return new RoleChangeResponse(user.getUserId(), nickname, newRole.name(), "Role successfully changed.");
    }


    // cerca tutti gli users di uno specifico progetto

    public List<Project_User_Role> getProjectMembers(Long projectId) {
        return project_user_roleRepository.findByProject_ProjectId(projectId);
    }

    // logica di permessi del progetto (collegamento e comparazione con Switch dei dati):

    public boolean hasPermission(Long projectId,
                                 Long userId,
                                 ProjectPermissionENUM projectPermission) {
        Project_User_Role relation = project_user_roleRepository
                .findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElse(null);

        if (relation == null) {
            return false;
        }

        RoleNameENUM role = relation.getRole().getRoleName();

        switch (projectPermission) {
            case VIEW:
                return true;
            case MODIFY, ADMIN_ACTIONS:
                return role == RoleNameENUM.CREATOR || role == RoleNameENUM.ADMIN;
            case CREATOR_ACTIONS:
                return role == RoleNameENUM.CREATOR;
            default:
                return false;
        }
    }

    // cambia ruolo ad uno specifico membro, controlli su ruolo Creator

    // ----------- HELPER PRIVATO -----------

    private void assignUserToProject(Long projectId, Long userId, RoleNameENUM roleName) {

        // trova il progetto

        Project foundProject = findProjectById(projectId);

        // trova lo user

        User foundUser = userService.findUserById(userId);

        // trova il ruolo

        User_Role userRole = user_roleRepository
                .findByRoleName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found."));

        // crea la relazione

        Project_User_Role relation = new Project_User_Role();
        relation.setProject(foundProject);
        relation.setUser(foundUser);
        relation.setRole(userRole);

        project_user_roleRepository.save(relation);
    }
}