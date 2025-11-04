package davidebraghi.CapstoneProject_TimelineManager.repositories;

import davidebraghi.CapstoneProject_TimelineManager.entities.Email_Notification;
import davidebraghi.CapstoneProject_TimelineManager.enums.EmailNotificationStatusENUM;
import davidebraghi.CapstoneProject_TimelineManager.enums.EmailNotificationTypeENUM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Email_NotificationRepository extends JpaRepository<Email_Notification, Long> {

    // cerca tutte le mail di notifica di uno specifico user

    List<Email_Notification> findByUser_UserId(Long userId);

    // cerca tutte le mail di notifica di uno specifico status

    List<Email_Notification> findByNotificationStatus(EmailNotificationStatusENUM notificationStatus);

    // cerca tutte le mail di notifica di uno specifico type

    List<Email_Notification> findByNotificationType(EmailNotificationTypeENUM notificationType);

    // cerca tutte le mail di notifica di uno specifico user che ha uno specifico status

    List<Email_Notification> findByUser_UserIdAndNotificationStatus(Long userId, EmailNotificationStatusENUM notificationStatus);

    // conta tutte le mail di notifica ricevute da uno specifico user

    Long countByUser_UserId(Long userId);
}