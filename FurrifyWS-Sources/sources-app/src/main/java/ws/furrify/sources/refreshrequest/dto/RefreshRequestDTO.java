package ws.furrify.sources.refreshrequest.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
@Builder(toBuilder = true)
@Value
@ToString
public class RefreshRequestDTO {
    Long id;

    UUID refreshRequestId;
    UUID artistId;
    UUID ownerId;

    RefreshRequestStatus status;

    ZonedDateTime createDate;
}
