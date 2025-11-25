package davidebraghi.CapstoneProject_TimelineManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import davidebraghi.CapstoneProject_TimelineManager.enums.RoleNameENUM;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_roles")
public class User_Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "roleId")
    private Long roleId;
    @Enumerated(EnumType.STRING)
    @Column(name = "roleName", nullable = false)
    private RoleNameENUM roleName;

    // relazioni

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ProjectMember> projectMembers;

    public User_Role(
            RoleNameENUM roleName
    ) {
        this.roleName = roleName;
    }


    // ======== Metodi utility ========

    public String getDisplayName() {
        return roleName.name();
    }

    public boolean isCreator() {
        return roleName == RoleNameENUM.CREATOR;
    }

    public boolean isManager() {
        return roleName == RoleNameENUM.ADMIN;
    }

    public boolean canManageProjects() {
        return roleName == RoleNameENUM.CREATOR ||
                roleName == RoleNameENUM.ADMIN;
    }

    public int getAssignmentCount() {
        return projectMembers != null ? projectMembers.size() : 0;
    }
}