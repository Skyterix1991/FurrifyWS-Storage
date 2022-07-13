package ws.furrify.sources.refreshrequest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.NoArtistSourcesFoundForRefreshException;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.artists.ArtistServiceClient;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDTO;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;
import ws.furrify.sources.source.SourceQueryRepository;

import java.time.ZonedDateTime;
import java.util.Objects;
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

        // TODO Handle sources with no content fetcher interface implemented, throw exception that no source has this option

        // Generate refresh request UUID
        UUID refreshRequestId = UUID.randomUUID();

        // Update refreshRequestDTO and create RefreshRequest aggregate from that data
        RefreshRequest refreshRequest = refreshRequestFactory.from(
                RefreshRequestDTO.builder()
                        .refreshRequestId(refreshRequestId)
                        .artistId(artistId)
                        .ownerId(ownerId)
                        .status(RefreshRequestStatus.PENDING)
                        .bearerToken(extractBearerTokenFromRequest())
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

    private String extractBearerTokenFromRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest().getHeader("Authorization");
    }
}
