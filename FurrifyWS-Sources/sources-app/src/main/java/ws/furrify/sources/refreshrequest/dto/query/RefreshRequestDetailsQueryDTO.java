package ws.furrify.sources.refreshrequest.dto.query;

import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface RefreshRequestDetailsQueryDTO extends Serializable {

    UUID getRefreshRequestId();

    UUID getArtistId();

    UUID getOwnerId();

    RefreshRequestStatus getStatus();

    ZonedDateTime getCreateDate();
}
