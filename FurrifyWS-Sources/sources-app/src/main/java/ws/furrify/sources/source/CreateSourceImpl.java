package ws.furrify.sources.source;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.artists.ArtistServiceClient;
import ws.furrify.sources.posts.PostServiceClient;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;
import ws.furrify.sources.source.dto.SourceDTO;

import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Log
final class CreateSourceImpl implements CreateSource {

    private final SourceFactory sourceFactory;
    private final DomainEventPublisher<SourceEvent> eventPublisher;
    private final SourceStrategyAttributeConverter sourceStrategyAttributeConverter;
    private final PostServiceClient postServiceClient;
    private final ArtistServiceClient artistServiceClient;

    @Override
    public UUID createSource(@NonNull final UUID ownerId,
                             @NonNull final SourceDTO sourceDTO) {
        // Generate source UUID
        UUID sourceId = UUID.randomUUID();

        switch (sourceDTO.getOriginType()) {
            case MEDIA -> {
                if (postServiceClient.getPostMedia(
                        sourceDTO.getOwnerId(),
                        sourceDTO.getPostId(),
                        sourceDTO.getOriginId()
                ) == null) {
                    throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(sourceDTO.getOriginId()));
                }
            }
            case ATTACHMENT -> {
                if (postServiceClient.getPostAttachment(
                        sourceDTO.getOwnerId(),
                        sourceDTO.getPostId(),
                        sourceDTO.getOriginId()
                ) == null) {
                    throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(sourceDTO.getOriginId()));
                }
            }
            case ARTIST -> {
                if (artistServiceClient.getUserArtist(
                        sourceDTO.getOwnerId(),
                        sourceDTO.getOriginId()
                ) == null) {
                    throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(sourceDTO.getOriginId()));
                }
            }
            default -> {
                log.severe("Origin type is missing.");
                throw new IllegalStateException("Origin type is missing.");
            }
        }

        // Update sourceDTO and create Source from that data
        Source source = sourceFactory.from(
                sourceDTO.toBuilder()
                        .sourceId(sourceId)
                        .postId(sourceDTO.getPostId())
                        .ownerId(ownerId)
                        .originId(sourceDTO.getOriginId())
                        .strategy(sourceDTO.getStrategy())
                        .data(sourceDTO.getData())
                        .originType(sourceDTO.getOriginType())
                        .createDate(ZonedDateTime.now())
                        .build(),
                true
        );

        // Publish create source event
        eventPublisher.publish(
                DomainEventPublisher.Topic.SOURCE,
                // Use ownerId as key
                ownerId,
                SourceUtils.createSourceEvent(
                        DomainEventPublisher.SourceEventType.CREATED,
                        source,
                        sourceStrategyAttributeConverter
                )
        );


        return sourceId;
    }
}
