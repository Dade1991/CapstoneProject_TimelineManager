package davidebraghi.CapstoneProject_TimelineManager.controllers;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectCreateRequest;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.Project_DTO_RequestsAndResponses.ProjectUpdateRequest;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project_User_Role;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.ValidationException;
import davidebraghi.CapstoneProject_TimelineManager.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // GET - FIND_ALL_PROJECT_BY_CREATOR_USER/CREATOR - http://localhost:3001/api/projects/creators/{creatorId}

    @GetMapping("/creators/{creatorId}")
    public List<Project> getProjectByCreatorId(
            @PathVariable Long creatorId
    ) {
        return projectService.findProjectsByCreatorId(creatorId);
    }

    // GET - FIND_ALL (paginato) - http://localhost:3001/api/projects

    @GetMapping
    public Page<Project> getAllProject(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "projectId") String sortBy
    ) {
        return projectService.findAllProject(pageNumber, pageSize, sortBy);
    }

    // GET - FIND_BY_ID  - http://localhost:3001/api/projects/{projectId}

    @GetMapping("/{projectId}")
    public Project getProjectById(
            @PathVariable Long projectId
    ) {
        return projectService.findProjectById(projectId);
    }

    // POST - SAVE - http://localhost:3001/api/projects

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(
            @RequestBody ProjectCreateRequest payload,
            BindingResult validationResult,
            @AuthenticationPrincipal User creator
    ) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().
                    stream().
                    map(fieldError -> fieldError.getDefaultMessage()).
                    toList());
        }
        return projectService.createProject(payload, creator.getUserId());
    }

    // PUT - FIND_BY_ID_AND_UPDATE - http://localhost:3001/api/projects/{projectId}

    @PutMapping("/{projectId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Project getProjectByIdAndUpdate(
            @PathVariable Long projectId,
            @RequestBody ProjectUpdateRequest payload,
            BindingResult validationResult
    ) {
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getFieldErrors().
                    stream().
                    map(fieldError -> fieldError.getDefaultMessage()).
                    toList());
        }
        return projectService.findProjectByIdAndUpdate(projectId, payload);
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