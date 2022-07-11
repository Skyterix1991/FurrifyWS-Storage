package ws.furrify.sources.notification.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.sources.vo.RemoteContent;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Skyte
 */
@Builder(toBuilder = true)
@Value
@ToString
public class NotificationDTO {
    Long id;

    UUID notificationId;

    UUID sourceId;

    UUID ownerId;

    List<RemoteContent> newRemoteContentList;

    Boolean viewed;

    ZonedDateTime createDate;
}
