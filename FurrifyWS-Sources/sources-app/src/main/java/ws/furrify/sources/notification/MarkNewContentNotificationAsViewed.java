package ws.furrify.sources.notification;

import java.util.UUID;

interface MarkNewContentNotificationAsViewed {

    void markNewContentNotificationAsViewed(final UUID ownerId, final UUID artistId, final UUID notificationId);

}
