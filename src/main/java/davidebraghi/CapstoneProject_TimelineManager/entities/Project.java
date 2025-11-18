package davidebraghi.CapstoneProject_TimelineManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "project_id", updatable = false)
    private Long projectId;
    @Column(name = "project_name")
    private String projectName;
    @Column(name = "project_description", columnDefinition = "TEXT")
    private String projectDescription;
    @Setter(AccessLevel.NONE)
    @Column(name = "creationDate", nullable = false)
    private LocalDate creationDate;
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    // relazioni

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Category> categories = new HashSet<>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creatorUserId", nullable = false)
    private User creator;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Task> tasks;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Project_User_Role> projectUserRoles;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Activity_Log> activities;

    public Project(String projectName,
                   String projectDescription,
                   LocalDate expiryDate) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.expiryDate = expiryDate;
    }

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDate.now();
    }

    // Metodi utility

    public int getTaskCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public boolean isCreator(User user) {
        return creator.getUserId().equals(user.getUserId());
    }

    public boolean isOverdue() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
}