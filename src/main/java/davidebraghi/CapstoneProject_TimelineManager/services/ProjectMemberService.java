package davidebraghi.CapstoneProject_TimelineManager.services;

import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.ProjectMember_DTO_RequestsAndResponses.ProjectMemberResponse;
import davidebraghi.CapstoneProject_TimelineManager.Payload_DTO.RoleChange_DTO_RequestsAndResponses.RoleChangeResponse;
import davidebraghi.CapstoneProject_TimelineManager.entities.Project;
import davidebraghi.CapstoneProject_TimelineManager.entities.ProjectMember;
import davidebraghi.CapstoneProject_TimelineManager.entities.User;
import davidebraghi.CapstoneProject_TimelineManager.entities.User_Role;
import davidebraghi.CapstoneProject_TimelineManager.enums.ProjectPermissionENUM;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import davidebraghi.CapstoneProject_TimelineManager.exceptions.NotFoundException;
import davidebraghi.CapstoneProject_TimelineManager.repositories.ProjectMemberRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.ProjectRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.Task_AssigneeRepository;
import davidebraghi.CapstoneProject_TimelineManager.repositories.User_RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectMemberService {

    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private Task_AssigneeRepository task_assigneeRepository;
    @Autowired
    private User_RoleRepository user_roleRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    public ProjectMemberService(ProjectMemberRepository projectMemberRepository,
                                Task_AssigneeRepository task_assigneeRepository,
                                User_RoleRepository user_roleRepository,
                                UserService userService,
                                ProjectRepository projectRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.task_assigneeRepository = task_assigneeRepository;
        this.user_roleRepository = user_roleRepository;
        this.userService = userService;
        this.projectRepository = projectRepository;
    }

    public List<ProjectMemberResponse> getProjectMembers(Long projectId) {
        List<ProjectMember> members = projectMemberRepository.findByProjectProjectId(projectId);

        return members.stream()
                .map(member -> {
                    long taskCount = task_assigneeRepository.countByTask_Project_ProjectIdAndUser_UserId(projectId, member.getUser().getUserId());
                    member.setTaskCount(taskCount);
                    return ProjectMemberResponse.from(member, taskCount);
                })
                .collect(Collectors.toList());
    }

    // controlla che uno user sia anche il creator di quel progetto

    public boolean isUserCreator(Long projectId, Long userId) {
        ProjectMember member = projectMemberRepository.findByProjectProjectIdAndUserUserId(projectId, userId);
        if (member == null) return false;
        return member.getRole().getRoleName() == RoleNameENUM.CREATOR;
    }

    // controlla che uno user sia anche un membro di quel progetto

    public boolean isUserMember(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectProjectIdAndUserUserId(projectId, userId) != null;
    }

    // aggiunge uno user al progetto con un ruolo specifico, senza duplicati

    public void addMemberToProject(Long projectId, Long userId, RoleNameENUM roleName) {
        ProjectMember existingMember = projectMemberRepository.findByProjectProjectIdAndUserUserId(projectId, userId);
        if (existingMember != null) {
            log.info("User {} is already member of project {}, skipping insertion.", userId, projectId);
            return;
        }
        Project project = projectRepository.getReferenceById(projectId);
        User foundUser = userService.findUserById(userId);
        User_Role userRole = user_roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found."));

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(foundUser);
        member.setRole(userRole);

        projectMemberRepository.save(member);
    }

    // rimuovere un membro da un progetto, impedisce di rimuove Creator

    public void removeMemberFromProject(Long projectId, Long userId) {
        if (isUserCreator(projectId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The Creator cannot be removed from the project.");
        }

        ProjectMember member = projectMemberRepository.findByProjectProjectIdAndUserUserId(projectId, userId);
        if (member == null) {
            throw new NotFoundException("User with ID " + userId + " not found in the project.");
        }

        projectMemberRepository.delete(member);
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

        ProjectMember member = projectMemberRepository.findByProjectProjectIdAndUserUserId(projectId, targetUserId);
        if (member == null) {
            throw new NotFoundException("User with ID " + targetUserId + " not found in the project.");
        }

        User_Role role = user_roleRepository.findByRoleName(newRole)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        member.setRole(role);
        projectMemberRepository.save(member);

        User user = member.getUser();
        String nickname = user.getNickname();

        return new RoleChangeResponse(user.getUserId(), nickname, newRole.name(), "Role successfully changed.");
    }

    // logica di permessi del progetto (collegamento e comparazione con Switch dei dati):

    public boolean hasPermission(Long projectId, Long userId, ProjectPermissionENUM projectPermission) {
        log.info("hasPermission called with projectId={}, userId={}, permission={}", projectId, userId, projectPermission);
        ProjectMember member = projectMemberRepository.findByProjectProjectIdAndUserUserId(projectId, userId);
        if (member == null) return false;

        log.info("No ProjectMember found for userId={} in projectId={}. Returning false.", userId, projectId);

        RoleNameENUM role = member.getRole().getRoleName();

        log.info("User role is {} for userId={} in projectId={}", role, userId, projectId);

        boolean result;
        switch (projectPermission) {
            case VIEW:
                result = true;
                break;
            case MODIFY:
            case ADMIN_ACTIONS:
                result = (role == RoleNameENUM.CREATOR || role == RoleNameENUM.ADMIN);
                break;
            case CREATOR_ACTIONS:
                result = (role == RoleNameENUM.CREATOR);
                break;
            default:
                result = false;
        }

        log.info("Permission check result: {}", result);
        return result;
    }
}