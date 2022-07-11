package ws.furrify.sources.notification.dto;

import lombok.RequiredArgsConstructor;
import ws.furrify.sources.notification.NotificationEvent;
import ws.furrify.sources.vo.RemoteContent;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Creates NotificationDTO from NotificationEvent.
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public class NotificationDtoFactory {

    private final NotificationQueryRepository notificationQueryRepository;

    public NotificationDTO from(UUID key, NotificationEvent notificationEvent) {
        Instant createDateInstant = notificationEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        var notificationId = UUID.fromString(notificationEvent.getNotificationId());
        var sourceId = UUID.fromString(notificationEvent.getData().getSourceId());

        return NotificationDTO.builder()
                .id(
                        notificationQueryRepository.getIdByNotificationId(notificationId)
                )
                .notificationId(notificationId)
                .sourceId(sourceId)
                .ownerId(key)
                .newRemoteContentList(
                        notificationEvent.getData().getRemoteContentList() != null ? notificationEvent.getData().getRemoteContentList().stream()
                                .map(remoteContentEventData -> {
                                    try {
                                        return new RemoteContent(null, remoteContentEventData.getContentIdentifier(), new URI(remoteContentEventData.getUri()));
                                    } catch (URISyntaxException e) {
                                        throw new RuntimeException("URI received in Source Remote Content is not valid.");
                                    }
                                })
                                .toList()
                                : new ArrayList<>()
                )
                .viewed(notificationEvent.getData().getViewed())
                .createDate(createDate)
                .build();
    }

}
