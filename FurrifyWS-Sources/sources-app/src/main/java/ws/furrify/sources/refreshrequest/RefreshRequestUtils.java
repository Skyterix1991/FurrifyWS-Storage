package ws.furrify.sources.refreshrequest;

import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestData;

import java.time.Instant;

/**
 * Utils class regarding RequestRefresh entity.
 *
 * @author Skyte
 */
class RefreshRequestUtils {

    /**
     * Create RefreshRequestEvent by given aggregate and set its state change.
     *
     * @param eventType      Event state change type.
     * @param refreshRequest RefreshRequest aggregate to build refresh request event from.
     * @return Created refresh request event.
     */
    public static RefreshRequestEvent createRefreshRequestEvent(final DomainEventPublisher.RefreshRequestEventType eventType,
                                                                final RefreshRequest refreshRequest) {
        RefreshRequestSnapshot refreshRequestSnapshot = refreshRequest.getSnapshot();

        return RefreshRequestEvent.newBuilder()
                .setState(eventType.name())
                .setRefreshRequestId(refreshRequestSnapshot.getRefreshRequestId().toString())
                .setData(
                        RefreshRequestData.newBuilder()
                                .setOwnerId(refreshRequestSnapshot.getOwnerId().toString())
                                .setArtistId(refreshRequestSnapshot.getArtistId().toString())
                                .setStatus(refreshRequestSnapshot.getStatus().toString())
                                .setCreateDate(refreshRequestSnapshot.getCreateDate().toInstant())
                                .build()
                )
                .setOccurredOn(Instant.now())
                .build();
    }

}
