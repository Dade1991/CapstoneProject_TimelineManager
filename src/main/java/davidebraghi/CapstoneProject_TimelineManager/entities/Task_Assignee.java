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
@Table(name = "task_assignees")
public class Task_Assignee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "task_assignees_id", updatable = false)
    private Long taskAssigneesId;
    @Setter(AccessLevel.NONE)
    @Column(name = "creationDate", nullable = false, updatable = false)
    private LocalDate creationDate;

    // relazioni

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "taskId", nullable = false)
    private Task task;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDate.now();
    }

    // Metodi utility

    public String getFullDescription() {
        return user.getNickname() + " assigned to " + task.getTaskTitle();
    }

    public boolean isSameAssignment(User otherUser, Task otherTask) {
        return this.user.getUserId().equals(otherUser.getUserId()) &&
                this.task.getTaskId().equals(otherTask.getTaskId());
    }
}