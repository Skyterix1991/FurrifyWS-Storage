package ws.furrify.sources.source;

import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;
import ws.furrify.sources.source.vo.SourceData;
import ws.furrify.sources.vo.RemoteContentData;

import java.time.Instant;

;

/**
 * Utils class regarding Source entity.
 *
 * @author Skyte
 */
class SourceUtils {

    /**
     * Create SourceEvent by given aggregate and set its state change.
     *
     * @param eventType Event state change type.
     * @param source    Source aggregate to build source event from.
     * @return Created source event.
     */
    public static SourceEvent createSourceEvent(final DomainEventPublisher.SourceEventType eventType,
                                                final Source source,
                                                final SourceStrategyAttributeConverter sourceStrategyAttributeConverter) {
        SourceSnapshot sourceSnapshot = source.getSnapshot();

        return SourceEvent.newBuilder()
                .setState(eventType.name())
                .setSourceId(sourceSnapshot.getSourceId().toString())
                .setData(
                        SourceData.newBuilder()
                                .setOwnerId(sourceSnapshot.getOwnerId().toString())
                                .setOriginId(sourceSnapshot.getOriginId().toString())
                                .setPostId(
                                        (sourceSnapshot.getPostId() != null) ? sourceSnapshot.getPostId().toString() : null
                                )
                                .setStrategy(
                                        sourceStrategyAttributeConverter.convertToDatabaseColumn(
                                                sourceSnapshot.getStrategy()
                                        )
                                )
                                .setRemoteContentList(sourceSnapshot.getRemoteContentList().stream()
                                        .map(remoteContent -> RemoteContentData.newBuilder()
                                                .setContentIdentifier(remoteContent.getContentIdentifier())
                                                .setUri(remoteContent.getUri().toString())
                                                .build())
                                        .toList()
                                )
                                .setDataHashMap(sourceSnapshot.getData())
                                .setOriginType(sourceSnapshot.getOriginType().name())
                                .setCreateDate(sourceSnapshot.getCreateDate().toInstant())
                                .build()
                )
                .setOccurredOn(Instant.now())
                .build();
    }

    /**
     * Create SourceEvent with REMOVE state.
     *
     * @param source Source aggregate to source values from.
     * @return Created source event.
     */
    public static SourceEvent deleteSourceEvent(final Source source) {
        var snapshot = source.getSnapshot();

        return SourceEvent.newBuilder()
                .setState(DomainEventPublisher.SourceEventType.REMOVED.name())
                .setSourceId(snapshot.getSourceId().toString())
                .setData(SourceData.newBuilder()
                        .setOriginId(snapshot.getOriginId().toString())
                        .setOriginType(snapshot.getOriginType().name())
                        .setPostId(
                                (snapshot.getPostId() != null) ? snapshot.getPostId().toString() : null
                        )
                        .build()
                )
                .setOccurredOn(Instant.now())
                .build();
    }

}
