package davidebraghi.CapstoneProject_TimelineManager.entities;

import davidebraghi.CapstoneProject_TimelineManager.enums.TaskPriorityENUM;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "task_id", updatable = false)
    private Long taskId;
    @Column(name = "task_title")
    private String taskTitle;
    @Column(name = "task_description")
    private String taskDescription;
    @Column(name = "task_priority")
    @Enumerated(EnumType.STRING)
    private TaskPriorityENUM taskPriority;
    @Setter(AccessLevel.NONE)
    @Column(name = "createdAt", nullable = false)
    private LocalDate createdAt;
    @Setter(AccessLevel.NONE)
    @Column(name = "updatedAt", nullable = false)
    private LocalDate updatedAt;
    @Column(name = "completedAt")
    private LocalDate completedAt;
    @Column(name = "expiryDate")
    private LocalDate taskExpiryDate;

    // relazioni

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "taskStatusId", nullable = false)
    private Task_Status status;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creatorUserId", nullable = false)
    private User creator;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Task_Assignee> assignees;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Activity_Log> activities;

    public Task(String taskTitle,
                String taskDescription,
                TaskPriorityENUM taskPriority,
                LocalDate taskExpiryDate) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskPriority = taskPriority;
        this.taskExpiryDate = taskExpiryDate;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    // Metodi utility

    public boolean isCompleted() {
        return completedAt != null;
    }

    public boolean isOverdue() {
        return taskExpiryDate != null &&
                taskExpiryDate.isBefore(LocalDate.now()) &&
                !isCompleted();
    }

    public boolean isAssignedTo(User user) {
        return assignees.stream()
                .anyMatch(ta -> ta.getUser().getUserId().equals(user.getUserId()));
    }

    public int getAssigneeCount() {
        return assignees != null ? assignees.size() : 0;
    }

    public int getCommentCount() {
        return comments != null ? comments.size() : 0;
    }

    public boolean canBeCompleted() {
        return !isCompleted() && status != null;
    }
}