package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.ProjectSave_DTO_RequestsAndResponse.ProjectSaveRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.ValidationException;
import davidebraghi.CapstoneProject_TimelineManager.services.ProjectMemberService;
import davidebraghi.CapstoneProject_TimelineManager.services.ProjectService;
import davidebraghi.CapstoneProject_TimelineManager.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ProjectMemberService projectMemberService;

    // GET - FIND_ALL_PROJECT_BY_CREATOR_USER/CREATOR - http://localhost:3001/api/projects/creators/{creatorId}

    @GetMapping("/creators/{creatorId}")
    public List<ProjectResponse> getProjectByCreatorId(@PathVariable Long creatorId) {
        List<Project> projects = projectService.findProjectsByCreatorId(creatorId);
        return projects.stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }

    // GET - FIND_ALL (per data di creazione ASC)

    @GetMapping
    public List<ProjectResponse> getAllProjectsOrdered() {
        return projectService.findAllProjectsOrdered();
    }

    // GET - FIND_BY_ID  - http://localhost:3001/api/projects/{projectId}

    @GetMapping("/{projectId}")
    public ProjectResponse getProjectById(@PathVariable Long projectId) {
        Project project = projectService.findProjectById(projectId);
        return ProjectResponse.fromEntity(project);
    }

    // POST - CREATE - http://localhost:3001/api/projects

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createProject(
            @RequestBody ProjectCreateRequest payload,
            BindingResult validationResult,
            @AuthenticationPrincipal User creator) {

        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().
                    stream().
                    map(fieldError -> fieldError.getDefaultMessage()).
                    toList());
        }
        Project savedProject = projectService.createProject(payload, creator.getUserId());
        projectMemberService.addMemberToProject(savedProject.getProjectId(), creator.getUserId(), RoleNameENUM.CREATOR);
        return ProjectResponse.fromEntity(savedProject);
    }

    // PUT - FIND_BY_ID_AND_UPDATE - http://localhost:3001/api/projects/{projectId}

    @PutMapping("/{projectId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@projectService.isUserCreator(#projectId, principal.userId) or @projectService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public ProjectResponse getProjectByIdAndUpdate(
            @PathVariable Long projectId,
            @RequestBody ProjectUpdateRequest payload,
            BindingResult validationResult) {

        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().
                    stream().
                    map(fieldError -> fieldError.getDefaultMessage()).
                    toList());
        }
        Project updatedProject = projectService.findProjectByIdAndUpdate(projectId, payload);
        return ProjectResponse.fromEntity(updatedProject);
    }

    // DELETE - FIND_BY_ID_AND_DELETE - http://localhost:3001/api/projects/{projectId}

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@projectService.isUserCreator(#projectId, principal.userId)")
    public void deleteProject(
            @PathVariable Long projectId
    ) {
        projectService.findProjectByIdAndDelete(projectId);
    }

//    SAVE - FOR CYCLE INTO TWO ARRAYS - http://localhost:3001/api/projects/{projectId}/save

    @PutMapping("/{projectId}/save")
    @ResponseStatus(HttpStatus.OK)
    public void saveProjectOrder(
            @PathVariable Long projectId,
            @RequestBody ProjectSaveRequest saveRequest
    ) {
        projectService.saveProjectOrder(projectId, saveRequest);
    }
}


//    ======= WAITING AREA =======


// GET - FIND_ALL (paginato) - http://localhost:3001/api/projects

//    @GetMapping
//    public Page<ProjectResponse> getAllProject(
//            @RequestParam(defaultValue = "0") int pageNumber,
//            @RequestParam(defaultValue = "10") int pageSize,
//            @RequestParam(defaultValue = "projectId") String sortBy) {
//
//        Page<Project> projectsPage = projectService.findAllProject(pageNumber, pageSize, sortBy);
//
//        List<ProjectResponse> projectsDTO = projectsPage.stream()
//                .map(ProjectResponse::fromEntity)
//                .collect(Collectors.toList());
//
//        return new PageImpl<>(projectsDTO, projectsPage.getPageable(), projectsPage.getTotalElements());
//    }
