package ws.furrify.sources.source.dto;

import lombok.RequiredArgsConstructor;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.source.SourceEvent;
import ws.furrify.sources.source.SourceQueryRepository;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;
import ws.furrify.sources.vo.RemoteContent;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Creates SourceDTO from SourceEvent.
 *
 * @author Skyte
 */
@RequiredArgsConstructor
public class SourceDtoFactory {

    private final SourceQueryRepository sourceQueryRepository;
    private final SourceStrategyAttributeConverter sourceStrategyAttributeConverter;

    public SourceDTO from(UUID key, SourceEvent sourceEvent) {
        Instant createDateInstant = sourceEvent.getData().getCreateDate();
        ZonedDateTime createDate = null;

        if (createDateInstant != null) {
            createDate = createDateInstant.atZone(ZoneId.systemDefault());
        }

        var sourceId = UUID.fromString(sourceEvent.getSourceId());
        var originId = UUID.fromString(sourceEvent.getData().getOriginId());
        // Post id can be null for artist origin id
        var postId =
                (sourceEvent.getData().getPostId() != null) ?
                        UUID.fromString(sourceEvent.getData().getPostId()) :
                        null;

        return SourceDTO.builder()
                .id(
                        sourceQueryRepository.getIdBySourceId(sourceId)
                )
                .postId(postId)
                .originId(originId)
                .sourceId(sourceId)
                .ownerId(key)
                .strategy(
                        (sourceEvent.getData().getStrategy() != null) ?
                                sourceStrategyAttributeConverter.convertToEntityAttribute(
                                        sourceEvent.getData().getStrategy()
                                )
                                : null
                )
                .originType(
                        SourceOriginType.valueOf(sourceEvent.getData().getOriginType())
                )
                .remoteContentList(
                        sourceEvent.getData().getRemoteContentList() != null ? sourceEvent.getData().getRemoteContentList().stream()
                                .map(remoteContentEventData -> {
                                    try {
                                        return new RemoteContent(null, remoteContentEventData.getContentIdentifier(), new URI(remoteContentEventData.getUri()));
                                    } catch (URISyntaxException e) {
                                        throw new RuntimeException("URI received in Source Remote Content is not valid.");
                                    }
                                })
                                .toList()
                                : new ArrayList<>()
                )
                .data(
                        (sourceEvent.getData().getDataHashMap() != null) ?
                                new HashMap<>(sourceEvent.getData().getDataHashMap()) :
                                null
                )
                .createDate(createDate)
                .build();
    }

}
