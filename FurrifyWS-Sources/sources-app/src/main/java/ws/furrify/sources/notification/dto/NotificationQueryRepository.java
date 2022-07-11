package ws.furrify.sources.notification.dto;

import java.util.UUID;

/**
 * @author Skyte
 */
public interface NotificationQueryRepository {
    Long getIdByNotificationId(UUID notificationId);
}
