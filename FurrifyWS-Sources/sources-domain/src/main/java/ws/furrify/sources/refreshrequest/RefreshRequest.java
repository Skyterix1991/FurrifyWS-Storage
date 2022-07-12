package ws.furrify.sources.refreshrequest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestBearerToken;
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
    private RefreshRequestStatus status;

    private RefreshRequestBearerToken bearerToken;

    private final ZonedDateTime createDate;

    static RefreshRequest restore(RefreshRequestSnapshot refreshRequestSnapshot) {
        return new RefreshRequest(
                refreshRequestSnapshot.getId(),
                refreshRequestSnapshot.getRefreshRequestId(),
                refreshRequestSnapshot.getArtistId(),
                refreshRequestSnapshot.getOwnerId(),
                refreshRequestSnapshot.getStatus(),
                RefreshRequestBearerToken.of(refreshRequestSnapshot.getBearerToken()),
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
                .bearerToken(bearerToken.getBearerToken())
                .createDate(createDate)
                .build();
    }

    void markInProgress() {
        if (status != RefreshRequestStatus.PENDING) {
            throw new IllegalStateException("You can only set status to IN_PROGRESS if current status is PENDING. Current status is " + status + ".");
        }

        this.status = RefreshRequestStatus.IN_PROGRESS;
    }

    void markCompleted() {
        if (status != RefreshRequestStatus.IN_PROGRESS) {
            throw new IllegalStateException("You can only set status to COMPLETED if current status is IN_PROGRESS. Current status is " + status + ".");
        }

        this.status = RefreshRequestStatus.COMPLETED;
    }

    void markFailed() {
        if (status != RefreshRequestStatus.IN_PROGRESS) {
            throw new IllegalStateException("You can only set status to FAILED if current status is IN_PROGRESS. Current status is " + status + ".");
        }

        this.status = RefreshRequestStatus.FAILED;
    }
}
