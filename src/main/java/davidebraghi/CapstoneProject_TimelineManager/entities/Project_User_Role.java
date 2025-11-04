package davidebraghi.CapstoneProject_TimelineManager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_user_roles",
        indexes = {
                @Index(name = "idx_pur_project", columnList = "projectId"),
                @Index(name = "idx_pur_user", columnList = "userId"),
                @Index(name = "idx_pur_role", columnList = "roleId")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"projectId", "userId", "roleId"},
                        name = "uk_project_user_role"
                )
        })
public class Project_User_Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "projectUserRoleId")
    private Long projectUserRoleId;
    @Column(name = "creationDate", nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDate creationDate;

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

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDate.now();
    }
}