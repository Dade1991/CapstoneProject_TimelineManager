package davidebraghi.CapstoneProject_TimelineManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "user_email"),
                @Index(name = "idx_users_nickname", columnList = "user_nickname")
        })
@JsonIgnoreProperties({"password", "authorities", "enabled", "accountNonLocked", "accountNonExpired", "credentialsNonExpired"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "user_id", updatable = false)
    private Long userId;
    @Column(name = "user_name", nullable = false, length = 50)
    private String name;
    @Column(name = "user_surname", nullable = false, length = 50)
    private String surname;
    @Column(name = "user_nickname", nullable = false, length = 50)
    private String nickname;
    @Column(name = "user_profilePicUrl")
    private String profilePicUrl;
    @Column(name = "user_email", unique = true, nullable = false, length = 50)
    private String email;
    @Column(name = "user_password", nullable = false, length = 50)
    private String password;
    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(name = "creationDate", nullable = false)
    private LocalDate creationDate;

    // relazioni

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Project> createdProjects;
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Task> createdTasks;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Project_User_Role> projectRoles;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Task_Assignee> assignedTasks;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Activity_Log> activities;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Email_Notification> notifications;

    public User(String name,
                String surname,
                String nickname,
                String email,
                String password) {
        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    // Metodi utility (No Database)

    public String getFullName() {
        return name + " " + surname;
    }

    public boolean hasProfilePic() {
        return profilePicUrl != null && !profilePicUrl.isEmpty();
    }

    public int getProjectCount() {
        return createdProjects != null ? createdProjects.size() : 0;
    }

    public int getTaskAssignmentCount() {
        return assignedTasks != null ? assignedTasks.size() : 0;
    }
}