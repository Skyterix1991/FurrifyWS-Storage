package ws.furrify.sources.source;

import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.vo.RemoteContent;
import ws.furrify.sources.vo.RemoteContentData;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * Utils class regarding Source remote content event.
 *
 * @author Skyte
 */
public class SourceRemoteContentUtils {

    /**
     * Create SourceEvent by given new remote content list and set its state change.
     *
     * @param eventType        Event state change type.
     * @param ownerId          Owner UUID.
     * @param sourceId         Source UUID.
     * @param remoteContentSet Remote content list to be set in source.
     * @return Created source remote content event.
     */
    public static SourceRemoteContentEvent createSourceRemoteContentEvent(final DomainEventPublisher.SourceRemoteContentEventType eventType,
                                                                          final UUID ownerId,
                                                                          final UUID sourceId,
                                                                          final Set<RemoteContent> remoteContentSet) {
        return SourceRemoteContentEvent.newBuilder()
                .setState(eventType.name())
                .setOwnerId(ownerId.toString())
                .setSourceId(sourceId.toString())
                .setRemoteContentList(remoteContentSet.stream()
                        .map(remoteContent -> RemoteContentData.newBuilder()
                                .setContentIdentifier(remoteContent.getContentIdentifier())
                                .setUri(remoteContent.getUri().toString())
                                .build())
                        .toList()
                )
                .setOccurredOn(Instant.now())
                .build();
    }

}
