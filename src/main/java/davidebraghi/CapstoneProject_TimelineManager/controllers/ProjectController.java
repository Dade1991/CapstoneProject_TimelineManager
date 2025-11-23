package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Member_DTO_RequestsAndResponses.MemberRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Member_DTO_RequestsAndResponses.MemberResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.RoleChange_DTO_RequestsAndResponses.RoleChangeRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.RoleChange_DTO_RequestsAndResponses.RoleChangeResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskResponse;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import davidebraghi.CapstoneProject_TimelineManager.entities.Task;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.ValidationException;
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

    // GET - FIND_ALL_PROJECT_BY_CREATOR_USER/CREATOR - http://localhost:3001/api/projects/creators/{creatorId}

    @GetMapping("/creators/{creatorId}")
    public List<ProjectResponse> getProjectByCreatorId(@PathVariable Long creatorId) {
        List<Project> projects = projectService.findProjectsByCreatorId(creatorId);
        return projects.stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }

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

    // ---------------- GESTIONE MEMBRI DEL PROGETTO ----------------

    // POST - aggiungere uno user al project [SOLO CREATOR]- http://localhost:3001/api/projects/{projectId}/members

    @PostMapping("/{projectId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@projectService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public void addMemberToProject(
            @PathVariable Long projectId,
            @RequestBody MemberRequest memberRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        projectService.addMemberToProject(projectId, memberRequest.userId(), memberRequest.role());
    }

    // DELETE - rimuovere un membro da un progetto [SOLO CREATOR] - http://localhost:3001/api/projects/{projectId}/members/{userId}

    @DeleteMapping("/{projectId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@projectService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public void removeMemberFromProject(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        projectService.removeMemberFromProject(projectId, userId);
    }

    // PUT - cambiare il ruolo ad uno specifico user [SOLO CREATOR] - http://localhost:3001/api/projects/{projectId}/members/{userId}/role

    @PutMapping("/{projectId}/members/{userId}/role")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@projectService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public RoleChangeResponse changeUserRole(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestBody RoleChangeRequest newRoleRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        return projectService.changeUserRole(projectId, currentUser.getUserId(), userId, newRoleRequest.newRole());
    }

    // GET - cerca tutti gli users di uno specifico progetto [ACCESSIBILE A MEMBRI E CREATOR] - http://localhost:3001/api/projects/{projectId}/members

    @GetMapping("/{projectId}/members")
    @PreAuthorize("@projectService.isUserMember(#projectId, principal.userId) or @projectService.isUserCreator(#projectId, principal.userId)")
    public List<MemberResponse> getProjectMembers(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User currentUser
    ) {
        return projectService.getProjectMembers(projectId);
    }

    // GET - restituisce tutti i tasks di uno specifico progetto - http://localhost:3001/api/projects/{projectId}/tasks

    @GetMapping("/{projectId}/tasks")
    @PreAuthorize("@projectService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public List<TaskResponse> getAllTasksByProject(@PathVariable Long projectId) {
        List<Task> tasks = taskService.findTaskByProject(projectId);
        return tasks.stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }
}