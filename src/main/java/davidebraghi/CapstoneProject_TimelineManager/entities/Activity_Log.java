package davidebraghi.CapstoneProject_TimelineManager.entities;

import davidebraghi.CapstoneProject_TimelineManager.enums.ActivityTypeENUM;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "activity_logs")
public class Activity_Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activityId")
    private Long activityId;
    @Column(name = "activityType", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityTypeENUM activityType;
    @Column(name = "activityDescription", columnDefinition = "TEXT", nullable = false)
    private String activityDescription;
    @CreationTimestamp
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // relazioni

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", nullable = true)
    private Project project;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taskId", nullable = true)
    private Task task;

    // activity LOGGER <Nickname> - <TipoAttività>: <Descrizione>

    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getNickname()).append(" - ");
        sb.append(activityType.name()).append(": ");
        sb.append(activityDescription);
        return sb.toString();
    }
}