package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.ProjectMember_DTO_RequestsAndResponses.ProjectMemberRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.ProjectMember_DTO_RequestsAndResponses.ProjectMemberResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.RoleChange_DTO_RequestsAndResponses.RoleChangeRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.RoleChange_DTO_RequestsAndResponses.RoleChangeResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Task_DTO_RequestsAndResponses.TaskResponse;
import davidebraghi.CapstoneProject_TimelineManager.entities.Task;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.services.ProjectMemberService;
import davidebraghi.CapstoneProject_TimelineManager.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectMemberController {

    @Autowired
    private ProjectMemberService projectMemberService;
    @Autowired
    private TaskService taskService;

    @Autowired
    public ProjectMemberController(ProjectMemberService projectMemberService, TaskService taskService) {
        this.projectMemberService = projectMemberService;
        this.taskService = taskService;
    }

    // ---------------- GESTIONE MEMBRI DEL PROGETTO ----------------

    // POST - aggiungere uno user al project [SOLO CREATOR]- http://localhost:3001/api/projects/{projectId}/members

    @PostMapping("/{projectId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@projectMemberService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public void addMemberToProject(
            @PathVariable Long projectId,
            @RequestBody ProjectMemberRequest memberRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        projectMemberService.addMemberToProject(projectId, memberRequest.userId(), memberRequest.role());
    }

    // DELETE - rimuovere un membro da un progetto [SOLO CREATOR] - http://localhost:3001/api/projects/{projectId}/members/{userId}

    @DeleteMapping("/{projectId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@projectMemberService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public void removeMemberFromProject(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser
    ) {
        projectMemberService.removeMemberFromProject(projectId, userId);
    }

    // PUT - cambiare il ruolo ad uno specifico user [SOLO CREATOR] - http://localhost:3001/api/projects/{projectId}/members/{userId}/role

    @PutMapping("/{projectId}/members/{userId}/role")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@projectMemberService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public RoleChangeResponse changeUserRole(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            @RequestBody RoleChangeRequest newRoleRequest,
            @AuthenticationPrincipal User currentUser
    ) {
        return projectMemberService.changeUserRole(projectId, currentUser.getUserId(), userId, newRoleRequest.newRole());
    }

    // GET - cerca tutti gli users di uno specifico progetto [ACCESSIBILE A MEMBRI E CREATOR] - http://localhost:3001/api/projects/{projectId}/members

    @GetMapping("/{projectId}/members")
    @PreAuthorize("@projectMemberService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public List<ProjectMemberResponse> getProjectMembers(
            @PathVariable Long projectId,
            @AuthenticationPrincipal User currentUser
    ) {
        return projectMemberService.getProjectMembers(projectId);
    }

    // GET - restituisce tutti i tasks di uno specifico progetto - http://localhost:3001/api/projects/{projectId}/tasks

    @GetMapping("/{projectId}/tasks")
    @PreAuthorize("@projectMemberService.hasPermission(#projectId, principal.userId, T(davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM).MODIFY)")
    public List<TaskResponse> getAllTasksByProject(
            @PathVariable Long projectId
    ) {
        List<Task> tasks = taskService.findTaskByProject(projectId);
        return tasks.stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    // GET - restituisce tutti i task per progetto Id e userId

    @GetMapping("/{projectId}/tasks/assignee/{userId}")
    public List<TaskResponse> getTasksByProjectAndAssignee(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        List<Task> tasks = taskService.findTasksByProjectAndUser(projectId, userId);
        return tasks.stream().map(TaskResponse::fromEntity).toList();
    }
}
