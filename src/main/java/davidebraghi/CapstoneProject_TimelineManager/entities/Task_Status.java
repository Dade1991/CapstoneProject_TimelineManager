package davidebraghi.CapstoneProject_TimelineManager.entities;

import davidebraghi.CapstoneProject_TimelineManager.enums.TaskStatusENUM;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_status")
public class Task_Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "task_status_id", updatable = false)
    private Long taskStatusId;
    @Column(name = "task_status_name", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatusENUM statusName;
    @Column(name = "orderIndex", nullable = false)
    private Integer orderIndex;

    // relazioni

    @OneToMany(mappedBy = "status", fetch = FetchType.LAZY)
    private List<Task> tasks;

    public Task_Status(TaskStatusENUM statusName, Integer orderIndex) {
        this.statusName = statusName;
        this.orderIndex = orderIndex;
    }

    // metodi utility

    public String getDisplayName() {
        return statusName.name();
    }

    public boolean isFinalStatus() {
        return statusName == TaskStatusENUM.COMPLETED ||
                statusName == TaskStatusENUM.PAUSED_OR_BLOCKED;
    }
}