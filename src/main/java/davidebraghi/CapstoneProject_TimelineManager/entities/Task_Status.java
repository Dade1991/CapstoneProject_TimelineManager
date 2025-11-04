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

    // relazioni

    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks;

    public Task_Status(
            TaskStatusENUM statusName
    ) {
        this.statusName = statusName;
    }
}