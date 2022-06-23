package ws.furrify.sources.refreshrequest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.NoArtistSourcesFoundForRefreshException;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.artists.ArtistServiceClient;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDTO;
import ws.furrify.sources.source.SourceQueryRepository;

import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Log
final class CreateRefreshRequestImpl implements CreateRefreshRequest {

    private final RefreshRequestFactory refreshRequestFactory;
    private final DomainEventPublisher<RefreshRequestEvent> eventPublisher;

    private final ArtistServiceClient artistServiceClient;

    private final SourceQueryRepository sourceQueryRepository;

    @Override
    public UUID createRefreshRequest(@NonNull final UUID ownerId,
                                     @NonNull final UUID artistId) {
        // Check if artist exists
        if (artistServiceClient.getUserArtist(
                ownerId,
                artistId
        ) == null) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(artistId));
        }

        // Check if artist contains any sources
        if (sourceQueryRepository.countByOwnerIdAndOriginId(ownerId, artistId) == 0) {
            throw new NoArtistSourcesFoundForRefreshException(Errors.NO_ARTIST_SOURCES_FOUND_FOR_REFRESH.getErrorMessage(artistId));
        }

        // Generate refresh request UUID
        UUID refreshRequestId = UUID.randomUUID();

        // Update refreshRequestDTO and create RefreshRequest aggregate from that data
        RefreshRequest refreshRequest = refreshRequestFactory.from(
                RefreshRequestDTO.builder()
                        .refreshRequestId(refreshRequestId)
                        .artistId(artistId)
                        .ownerId(ownerId)
                        .createDate(ZonedDateTime.now())
                        .build()
        );

        // Publish create refresh request event
        eventPublisher.publish(
                DomainEventPublisher.Topic.REFRESH_REQUEST,
                // Use ownerId as key
                ownerId,
                RefreshRequestUtils.createRefreshRequestEvent(
                        DomainEventPublisher.RefreshRequestEventType.CREATED,
                        refreshRequest
                )
        );


        return refreshRequestId;
    }
}
