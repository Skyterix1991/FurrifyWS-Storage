package ws.furrify.sources.notification.dto.query;

import ws.furrify.sources.vo.RemoteContent;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface NewContentNotificationDetailsQueryDTO extends Serializable {

    UUID getNotificationId();

    UUID getSourceId();

    UUID getOwnerId();

    List<RemoteContent> getNewRemoteContentList();

    Boolean getViewed();

    ZonedDateTime getCreateDate();

}
