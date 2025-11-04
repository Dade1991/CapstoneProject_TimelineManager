package davidebraghi.CapstoneProject_TimelineManager.entities;

import davidebraghi.CapstoneProject_TimelineManager.enums.EmailNotificationStatusENUM;
import davidebraghi.CapstoneProject_TimelineManager.enums.EmailNotificationTypeENUM;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "email_notifications")
public class Email_Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_notification_id")
    private Long emailNotificationId;
    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmailNotificationTypeENUM notificationType;
    @Column(name = "notificationStatus", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmailNotificationStatusENUM notificationStatus;
    @Column(name = "email_subject", nullable = false)
    private String emailSubject;
    @Column(name = "email_body", nullable = false)
    private String emailBody;
    @Column(name = "sendDate", nullable = false)
    private LocalDateTime sendDate;
    @Column(name = "sentAt")
    private LocalDateTime sentAt;
    @Column(name = "errorMessage")
    private String errorMessage;

    // relazioni

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        sendDate = LocalDateTime.now();
        if (notificationStatus == null) {
            notificationStatus = EmailNotificationStatusENUM.PENDING;
        }
    }

    // logica invio email

    public boolean isPending() {
        return notificationStatus == EmailNotificationStatusENUM.PENDING;
    }

    public boolean isSent() {
        return notificationStatus == EmailNotificationStatusENUM.SENT;
    }

    public boolean isFailed() {
        return notificationStatus == EmailNotificationStatusENUM.FAILED;
    }

    public void markAsSent() {
        this.notificationStatus = EmailNotificationStatusENUM.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markAsFailed(String error) {
        this.notificationStatus = EmailNotificationStatusENUM.FAILED;
        this.errorMessage = error;
    }
}