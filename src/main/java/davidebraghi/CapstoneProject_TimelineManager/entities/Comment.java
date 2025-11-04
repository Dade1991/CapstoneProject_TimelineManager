package davidebraghi.CapstoneProject_TimelineManager.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "commentId")
    private Long commentId;
    @Column(name = "commentText")
    private String commentText;
    @Column(name = "createdAt", nullable = false)
    private LocalDate createdAt;
    @Column(name = "updatedAt", nullable = false)
    private LocalDate updatedAt;

    // relazioni

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "taskId", nullable = true)
    private Task task;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "projectId", nullable = true)
    private Project project;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
}