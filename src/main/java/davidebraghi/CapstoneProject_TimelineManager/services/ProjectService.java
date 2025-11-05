package davidebraghi.CapstoneProject_TimelineManager.services;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
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

    public Project createdProject(ProjectCreateRequest payload, Long creatorId) {

        // trovare uno user creatore

        User creator = userService.findUserById(creatorId);

        // crea un progetto

        Project newProject = new Project(
                payload.projectName(),
                payload.projectDescription(),
                payload.expiryDate()
        );
        newProject.setCreator(creator);

        // salvare un progetto

        Project savedProject = projectRepository.save(newProject);

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

    public Project findProjectByIdAndUpdate(Long projectId, ProjectUpdateRequest paylod, Long userId) {

        Project project = findProjectById(projectId);

        if(!project.isCreator(userService.findUserById(userId)) && )
    }
    
    // FIND_BY_ID_AND_DELETE

}
