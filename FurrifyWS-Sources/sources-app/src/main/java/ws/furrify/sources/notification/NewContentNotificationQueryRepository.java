package ws.furrify.sources.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ws.furrify.sources.notification.dto.query.NewContentNotificationDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface NewContentNotificationQueryRepository {
    Long getIdByNotificationId(UUID notificationId);

    Optional<NewContentNotificationDetailsQueryDTO> findByOwnerIdAndSourceIdAndNotificationId(UUID userId, UUID sourceId, UUID notificationId);

    Page<NewContentNotificationDetailsQueryDTO> findAllByOwnerId(UUID userId, Pageable pageable);
}
