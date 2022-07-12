package ws.furrify.sources.notification;

import ws.furrify.sources.notification.dto.NewContentNotificationDTO;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

final class NotificationFactory {

    NewContentNotification from(NewContentNotificationDTO newContentNotificationDTO) {

        NewContentNotificationSnapshot snapshot = NewContentNotificationSnapshot.builder()
                .id(newContentNotificationDTO.getId())
                .notificationId(newContentNotificationDTO.getNotificationId() != null ? newContentNotificationDTO.getNotificationId() : UUID.randomUUID())
                .sourceId(newContentNotificationDTO.getSourceId())
                .ownerId(newContentNotificationDTO.getOwnerId())
                .newRemoteContentList
                        (newContentNotificationDTO.getNewRemoteContentList() != null ? newContentNotificationDTO.getNewRemoteContentList() : new ArrayList<>())
                .viewed(newContentNotificationDTO.getViewed())
                .createDate(
                        newContentNotificationDTO.getCreateDate() != null ? newContentNotificationDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return NewContentNotification.restore(snapshot);
    }

}
