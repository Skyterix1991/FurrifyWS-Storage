package ws.furrify.sources.refreshrequest.dto;

import lombok.RequiredArgsConstructor;
import ws.furrify.sources.refreshrequest.RefreshRequestEvent;
import ws.furrify.sources.refreshrequest.RefreshRequestQueryRepository;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Creates RefreshRequestDTO from RefreshRequestEvent.
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public class RefreshRequestDtoFactory {

    private final RefreshRequestQueryRepository refreshRequestQueryRepository;

    public RefreshRequestDTO from(UUID key, RefreshRequestEvent refreshRequestEvent) {
        Instant createDateInstant = refreshRequestEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        var refreshRequestId = UUID.fromString(refreshRequestEvent.getRefreshRequestId());
        var artistId = UUID.fromString(refreshRequestEvent.getData().getArtistId());
        var ownerId = UUID.fromString(refreshRequestEvent.getData().getOwnerId());

        return RefreshRequestDTO.builder()
                .id(
                        refreshRequestQueryRepository.getIdByRefreshRequestId(refreshRequestId)
                )
                .artistId(artistId)
                .ownerId(key)
                .status(
                        RefreshRequestStatus.valueOf(refreshRequestEvent.getData().getStatus())
                )
                .createDate(createDate)
                .build();
    }

}
