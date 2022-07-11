package ws.furrify.sources.notification;

import ws.furrify.sources.notification.dto.NotificationDTO;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

final class NotificationFactory {

    NewContentNotification from(NotificationDTO notificationDTO) {

        NewContentNotificationSnapshot snapshot = NewContentNotificationSnapshot.builder()
                .id(notificationDTO.getId())
                .notificationId(notificationDTO.getNotificationId() != null ? notificationDTO.getNotificationId() : UUID.randomUUID())
                .sourceId(notificationDTO.getSourceId())
                .ownerId(notificationDTO.getOwnerId())
                .newRemoteContentList
                        (notificationDTO.getNewRemoteContentList() != null ? notificationDTO.getNewRemoteContentList() : new ArrayList<>())
                .viewed(notificationDTO.getViewed())
                .createDate(
                        notificationDTO.getCreateDate() != null ? notificationDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return NewContentNotification.restore(snapshot);
    }

}
