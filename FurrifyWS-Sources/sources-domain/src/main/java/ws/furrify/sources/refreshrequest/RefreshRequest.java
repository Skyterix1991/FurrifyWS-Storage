package ws.furrify.sources.refreshrequest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
class RefreshRequest {
    private final Long id;
    @NonNull
    private final UUID refreshRequestId;

    @NonNull
    private final UUID artistId;

    @NonNull
    private final UUID ownerId;

    @NonNull
    private final RefreshRequestStatus status;

    private final ZonedDateTime createDate;

    static RefreshRequest restore(RefreshRequestSnapshot refreshRequestSnapshot) {
        return new RefreshRequest(
                refreshRequestSnapshot.getId(),
                refreshRequestSnapshot.getRefreshRequestId(),
                refreshRequestSnapshot.getArtistId(),
                refreshRequestSnapshot.getOwnerId(),
                refreshRequestSnapshot.getStatus(),
                refreshRequestSnapshot.getCreateDate()
        );
    }

    RefreshRequestSnapshot getSnapshot() {
        return RefreshRequestSnapshot.builder()
                .id(id)
                .refreshRequestId(refreshRequestId)
                .artistId(artistId)
                .ownerId(ownerId)
                .status(status)
                .createDate(createDate)
                .build();
    }
}
