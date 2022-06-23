package ws.furrify.sources.refreshrequest;

import ws.furrify.sources.refreshrequest.dto.RefreshRequestDTO;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

final class RefreshRequestFactory {

    RefreshRequest from(RefreshRequestDTO refreshRequestDTO) {

        RefreshRequestSnapshot snapshot = RefreshRequestSnapshot.builder()
                .id(refreshRequestDTO.getId())
                .refreshRequestId(refreshRequestDTO.getRefreshRequestId() != null ? refreshRequestDTO.getRefreshRequestId() : UUID.randomUUID())
                .artistId(refreshRequestDTO.getArtistId())
                .ownerId(refreshRequestDTO.getArtistId())
                .status(RefreshRequestStatus.PENDING)
                .createDate(
                        refreshRequestDTO.getCreateDate() != null ? refreshRequestDTO.getCreateDate() : ZonedDateTime.now()
                )
                .build();

        return RefreshRequest.restore(snapshot);
    }

}
