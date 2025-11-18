package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project_User_Role;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.ValidationException;
import davidebraghi.CapstoneProject_TimelineManager.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // GET - FIND_ALL_PROJECT_BY_CREATOR_USER/CREATOR - http://localhost:3001/api/projects/creators/{creatorId}

    @GetMapping("/creators/{creatorId}")
    public List<ProjectResponse> getProjectByCreatorId(@PathVariable Long creatorId) {
        List<Project> projects = projectService.findProjectsByCreatorId(creatorId);
        return projects.stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }
    // GET - FIND_ALL (paginato) - http://localhost:3001/api/projects

    @GetMapping
    public Page<ProjectResponse> getAllProject(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "projectId") String sortBy) {

        Page<Project> projectsPage = projectService.findAllProject(pageNumber, pageSize, sortBy);

        List<ProjectResponse> projectsDTO = projectsPage.stream()
                .map(ProjectResponse::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(projectsDTO, projectsPage.getPageable(), projectsPage.getTotalElements());
    }

    // GET - FIND_BY_ID  - http://localhost:3001/api/projects/{projectId}

    @GetMapping("/{projectId}")
    public ProjectResponse getProjectById(@PathVariable Long projectId) {
        Project project = projectService.findProjectById(projectId);
        return ProjectResponse.fromEntity(project);
    }

    // POST - SAVE - http://localhost:3001/api/projects

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
        return ProjectResponse.fromEntity(savedProject);
    }

    // PUT - FIND_BY_ID_AND_UPDATE - http://localhost:3001/api/projects/{projectId}

    @PutMapping("/{projectId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
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
    public void deleteProject(
            @PathVariable Long projectId
    ) {
        projectService.findProjectByIdAndDelete(projectId);
    }

    // ---------------- GESTIONE MEMBRI DEL PROGETTO ----------------

    // POST - aggiungere uno user al project - http://localhost:3001/api/projects/{projectId}/members/{userId}

    @PostMapping("/{projectId}/members/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addMemberToProject(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestParam RoleNameENUM roleName
    ) {
        projectService.addMemberToProject(projectId, userId, roleName);
    }

    // DELETE - rimuovere un membro da un progetto - http://localhost:3001/api/projects/{projectId}/members/{userId}

    @DeleteMapping("/{projectId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMemberFromProject(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        projectService.removeMemberFromProject(projectId, userId);
    }

    // PUT - cambiare il ruolo ad uno specifico user - http://localhost:3001/api/projects/{projectId}/members/{userId}/role

    @PutMapping("/{projectId}/members/{userId}/role")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void changeUserRole(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestParam RoleNameENUM newRole
    ) {
        projectService.changeUserRole(projectId, userId, newRole);
    }

    // GET - cerca tutti gli users di uno specifico progetto - http://localhost:3001/api/projects/{projectId}/members

    @GetMapping("/{projectId}/members")
    public List<Project_User_Role> getProjectMembers(
            @PathVariable Long projectId
    ) {
        return projectService.getProjectMembers(projectId);
    }
}