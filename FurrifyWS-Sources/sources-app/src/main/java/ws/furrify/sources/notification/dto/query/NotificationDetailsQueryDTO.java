package ws.furrify.sources.notification.dto.query;

import ws.furrify.sources.vo.RemoteContent;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface NotificationDetailsQueryDTO extends Serializable {

    UUID getNotificationId();

    UUID getSourceId();

    UUID getOwnerId();

    HashMap<String, String> getData();

    List<RemoteContent> getNewRemoteContentList();

    Boolean getViewed();

    ZonedDateTime getCreateDate();

}
