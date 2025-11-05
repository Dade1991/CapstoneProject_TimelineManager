package davidebraghi.CapstoneProject_TimelineManager.services;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project_User_Role;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.entities.User_Role;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.BadRequestException;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.NotFoundException;
import davidebraghi.CapstoneProject_TimelineManager.repositories.ProjectRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.Project_User_RoleRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.User_RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public Project createProject(ProjectCreateRequest payload, Long creatorId) {
        log.info("Creazione progetto per creatore: {}", creatorId);

        User foundCreator = userService.findUserById(creatorId);

        Project project = new Project(
                payload.projectName(),
                payload.projectDescription(),
                payload.expiryDate()
        );
        project.setCreator(foundCreator);

        Project savedProject = projectRepository.save(project);

        // Assegnare il creatore come ADMIN
        assignUserToProject(savedProject.getProjectId(), creatorId, RoleNameENUM.ADMIN);

        return savedProject;
    }

    // FIND_BY_ID

    public Project findProjectById(Long projectId) {
        return this.projectRepository.
                findById(projectId).
                orElseThrow(() -> new NotFoundException("Progetto con ID " + projectId + " non trovato"));
    }

    // FIND_ALL_PROJECT_BY_CREATOR_USER

    public List<Project> findProjectsByCreatorId(Long userId) {
        return this.projectRepository.
                findByCreator_UserId(userId);
    }

    // FIND_BY_ID_AND_UPDATE

    public Project findProjectByIdAndUpdate(Long projectId, ProjectUpdateRequest payload, Long userId) {

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

    public void deleteProject(Long projectId) {
        Project foundProject = findProjectById(projectId);

        this.projectRepository.delete(foundProject);
    }

    // ---------------- GESTIONE MEMBRI DEL PROGETTO ----------------

    // aggiungere uno user al project

    public void addMemberToProject(Long projectId, Long userId, RoleNameENUM roleName) {

        Project foundProject = findProjectById(projectId);
        User foundUser = userService.findUserById(userId);

        // verificare che non sia già membro

        if (project_user_roleRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId).isPresent()) {
            throw new BadRequestException("The user with ID " + userId + " has been already assigned to this project.");
        }

        // trovare il ruolo

        User_Role userRole = user_roleRepository.
                findByRoleName(roleName).
                orElseThrow(() -> new NotFoundException("Role not found."));

        // Creare la relazione

        Project_User_Role relation = new Project_User_Role();
        relation.setProject(foundProject);
        relation.setUser(foundUser);
        relation.setRole(userRole);

        project_user_roleRepository.save(relation);
    }

    // rimuovere un membro da un progetto

    public void removeMemberFromProject(Long projectId, Long userId) {

        Project_User_Role relation = project_user_roleRepository.
                findByProject_ProjectIdAndUser_UserId(projectId, userId).
                orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found in the project."));

        project_user_roleRepository.delete(relation);
    }

    // cambiare il ruolo ad uno specifico user

    public void changeUserRole(Long projectId, Long userId, RoleNameENUM newRole) {

        Project_User_Role relation = project_user_roleRepository.
                findByProject_ProjectIdAndUser_UserId(projectId, userId).
                orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found in the project."));

        User_Role role = user_roleRepository.
                findByRoleName(newRole).
                orElseThrow(() -> new NotFoundException("Role not found."));

        relation.setRole(role);
        project_user_roleRepository.save(relation);
    }

    // cerca tutti gli users di uno specifico progetto

    public List<Project_User_Role> getProjectMembers(Long projectId) {
        return project_user_roleRepository.findByProject_ProjectId(projectId);
    }

    // ----------- HELPER PRIVATO -----------

    private void assignUserToProject(Long projectId, Long userId, RoleNameENUM roleName) {

        // trova il progetto

        Project foundProject = findProjectById(projectId);

        // trova lo user

        User foundUser = userService.findUserById(userId);

        // trova il ruolo

        User_Role userRole = user_roleRepository
                .findByRoleName(roleName)
                .orElseThrow(() -> new NotFoundException("Ruolo non trovato"));

        // crea la relazione

        Project_User_Role relation = new Project_User_Role();
        relation.setProject(foundProject);
        relation.setUser(foundUser);
        relation.setRole(userRole);

        project_user_roleRepository.save(relation);
    }
}