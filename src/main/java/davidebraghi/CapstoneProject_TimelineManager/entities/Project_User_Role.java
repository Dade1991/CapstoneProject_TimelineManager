package davidebraghi.CapstoneProject_TimelineManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_user_roles")
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
    @JsonIgnore
    private User_Role role;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDate.now();
    }
}