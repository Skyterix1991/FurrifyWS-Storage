package ws.furrify.sources.notification;

import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.notification.vo.NewContentNotificationData;
import ws.furrify.sources.vo.RemoteContentData;

import java.time.Instant;

/**
 * Utils class regarding NewContentNotification entity.
 *
 * @author Skyte
 */
class NewContentNotificationUtils {

    /**
     * Create NewContentNotificationEvent by given aggregate and set its state change.
     *
     * @param eventType              Event state change type.
     * @param newContentNotification NewContentNotification aggregate to build new content notification event from.
     * @return Created new content notification event.
     */
    public static NewContentNotificationEvent createNewContentNotificationEvent(final DomainEventPublisher.RefreshRequestEventType eventType,
                                                                                final NewContentNotification newContentNotification) {
        NewContentNotificationSnapshot newContentNotificationSnapshot = newContentNotification.getSnapshot();

        return NewContentNotificationEvent.newBuilder()
                .setState(eventType.name())
                .setNotificationId(newContentNotificationSnapshot.getNotificationId().toString())
                .setData(
                        NewContentNotificationData.newBuilder()
                                .setOwnerId(newContentNotificationSnapshot.getOwnerId().toString())
                                .setSourceId(newContentNotificationSnapshot.getSourceId().toString())
                                .setViewed(newContentNotificationSnapshot.isViewed())
                                .setRemoteContentList(
                                        newContentNotificationSnapshot.getNewRemoteContentList().stream()
                                                .map(remoteContent -> RemoteContentData.newBuilder()
                                                        .setContentIdentifier(remoteContent.getContentIdentifier())
                                                        .setUri(remoteContent.getUri().toString())
                                                        .build()).toList()
                                )
                                .setCreateDate(newContentNotificationSnapshot.getCreateDate().toInstant())
                                .build()
                )
                .setOccurredOn(Instant.now())
                .build();
    }

}
