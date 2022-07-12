package ws.furrify.sources.notification;

import java.util.Optional;
import java.util.UUID;

interface NewContentNotificationRepository {
    NewContentNotification save(NewContentNotification refreshRequest);

    Optional<NewContentNotification> findByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);
}
