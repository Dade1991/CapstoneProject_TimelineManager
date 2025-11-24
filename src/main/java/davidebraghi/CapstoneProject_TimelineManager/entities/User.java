package davidebraghi.CapstoneProject_TimelineManager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@JsonIgnoreProperties({"password", "authorities", "enabled", "accountNonLocked", "accountNonExpired", "credentialsNonExpired"})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "user_id", updatable = false)
    private Long userId;
    @Column(name = "user_name", nullable = false, length = 50)
    private String name;
    @Column(name = "user_surname", nullable = false, length = 50)
    private String surname;
    @Column(name = "user_nickname", unique = true, nullable = false, length = 50)
    private String nickname;
    @Column(name = "user_email", unique = true, nullable = false, length = 50)
    private String email;
    @Column(name = "user_password", nullable = false, length = 100)
    private String password;
    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(name = "creationDate", nullable = false)
    private LocalDate creationDate;
    @Column(name = "avatar_url", nullable = true)
    private String avatarUrl;

    // relazioni

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Project> createdProjects;
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Task> createdTasks;
    //    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    @JsonIgnore
//    private List<Project_User_Role> projectRoles;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Task_Assignee> assignedTasks;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Comment> comments;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Activity_Log> activities;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<Email_Notification> notifications;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ProjectMember> projectMembers;

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

    public boolean hasAvatarProfilePic() {
        return avatarUrl != null && !avatarUrl.isEmpty();
    }

    public int getProjectCount() {
        return createdProjects != null ? createdProjects.size() : 0;
    }

    public int getTaskAssignmentCount() {
        return assignedTasks != null ? assignedTasks.size() : 0;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (projectMembers == null || projectMembers.isEmpty()) {

            // Ritorna la lista di autorità (ruoli)

            return List.of(new SimpleGrantedAuthority("ROLE_USER")); // ruolo base
        }
        return projectMembers.stream()
                .map(projectMember -> new SimpleGrantedAuthority("ROLE_" + projectMember.getRole().getRoleName().name()))
                .distinct()
                .toList();
    }

    // implementazioni di userDetails

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}