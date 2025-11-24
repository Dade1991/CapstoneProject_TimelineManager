package davidebraghi.CapstoneProject_TimelineManager.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_members")
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "projectMemberId", updatable = false)
    private Long projectMemberId;
    @Column(name = "creationDate", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime creationDate;

    // relazioni

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roleId", nullable = false)
    private User_Role role;

    @Transient
    private long taskCount;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }

    // Metodi utility

    public String getFullDescription() {
        return user.getNickname() + " in project " + project.getProjectName() + " role " + role.getRoleName();
    }
}