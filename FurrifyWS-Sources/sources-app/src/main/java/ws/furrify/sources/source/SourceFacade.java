package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.source.dto.SourceDTO;
import ws.furrify.sources.source.dto.SourceDtoFactory;
import ws.furrify.sources.source.vo.RemoteContent;
import ws.furrify.sources.source.vo.RemoteContentData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
final public class SourceFacade {

    private final CreateSource createSourceImpl;
    private final DeleteSource deleteSourceImpl;
    private final UpdateSource updateSourceImpl;
    private final ReplaceSourceImpl replaceSourceImpl;
    private final SourceRepository sourceRepository;
    private final SourceFactory sourceFactory;
    private final SourceDtoFactory sourceDTOFactory;

    /**
     * Handle incoming source events.
     *
     * @param sourceEvent Source event instance received from kafka.
     */
    public void handleEvent(final UUID key, final SourceEvent sourceEvent) {
        SourceDTO sourceDTO = sourceDTOFactory.from(key, sourceEvent);

        switch (DomainEventPublisher.SourceEventType.valueOf(sourceEvent.getState())) {
            case CREATED, REPLACED, UPDATED -> saveSourceToDatabase(sourceDTO);
            case REMOVED -> deleteSourceBySourceIdFromDatabase(sourceDTO.getSourceId());

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + sourceEvent.getState() + " Topic=source_events");
        }
    }

    /**
     * Handle incoming source remote content events.
     *
     * @param sourceRemoteContentEvent Source remote content event instance received from kafka.
     */
    public void handleEvent(final UUID key, final SourceRemoteContentEvent sourceRemoteContentEvent) {
        switch (DomainEventPublisher.SourceRemoteContentEventType.valueOf(sourceRemoteContentEvent.getState())) {
            case REPLACED -> replaceSourceRemoteContentInDatabase(
                    UUID.fromString(sourceRemoteContentEvent.getOwnerId()),
                    UUID.fromString(sourceRemoteContentEvent.getSourceId()),
                    sourceRemoteContentEvent.getRemoteContentList()
            );

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + sourceRemoteContentEvent.getState() + " Topic=source_remote_content_events");
        }
    }

    /**
     * Creates Source.
     *
     * @param ownerId   Owner of source to be created.
     * @param sourceDTO SourceDTO.
     * @return Created record UUID.
     */
    public UUID createSource(UUID ownerId, SourceDTO sourceDTO) {
        return createSourceImpl.createSource(ownerId, sourceDTO);
    }

    /**
     * Replaces all fields in Source.
     *
     * @param ownerId   Owner UUID of Source.
     * @param sourceId  Source UUID to be replaced.
     * @param sourceDTO SourceDTO with replace details.
     */
    public void replaceSource(UUID ownerId, UUID sourceId, SourceDTO sourceDTO) {
        replaceSourceImpl.replaceSource(ownerId, sourceId, sourceDTO);
    }

    /**
     * Updates changed fields in Source from DTO.
     *
     * @param ownerId   Owner UUID of Source.
     * @param sourceId  Source UUID to be updated.
     * @param sourceDTO SourceDTO with some changes.
     */
    public void updateSource(UUID ownerId, UUID sourceId, SourceDTO sourceDTO) {
        updateSourceImpl.updateSource(ownerId, sourceId, sourceDTO);
    }

    /**
     * Deletes Source.
     *
     * @param ownerId  Owner UUID of Source.
     * @param sourceId Source UUID.
     */
    public void deleteSource(final UUID ownerId, final UUID sourceId) {
        deleteSourceImpl.deleteSource(ownerId, sourceId);
    }

    private void deleteSourceBySourceIdFromDatabase(final UUID sourceId) {
        sourceRepository.deleteBySourceId(sourceId);
    }

    private void saveSourceToDatabase(final SourceDTO sourceDTO) {
        sourceRepository.save(sourceFactory.from(sourceDTO));
    }

    private void replaceSourceRemoteContentInDatabase(
            final UUID ownerId,
            final UUID sourceId,
            final List<RemoteContentData> remoteContentList
    ) {
        Source source = sourceRepository.findByOwnerIdAndSourceId(ownerId, sourceId)
                .orElseThrow(() -> new IllegalStateException("Event contains not existing owner id and source id combo."));
        source.replaceRemoteContent(
                remoteContentList.stream()
                        .map(remoteContentEventData -> {
                            try {
                                return new RemoteContent(null, remoteContentEventData.getContentIdentifier(), new URI(remoteContentEventData.getUri()));
                            } catch (URISyntaxException e) {
                                throw new RuntimeException("URI received in Source Remote Content is not valid.");
                            }
                        })
                        .toList()
        );

        sourceRepository.save(source);
    }

}
