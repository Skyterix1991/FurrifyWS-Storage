package ws.furrify.sources.refreshrequest;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.shared.cache.CacheManager;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.notification.NewContentNotificationEvent;
import ws.furrify.sources.notification.NewContentNotificationUtils;
import ws.furrify.sources.notification.dto.NewContentNotificationDTO;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDTO;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;
import ws.furrify.sources.source.SourceQueryRepository;
import ws.furrify.sources.source.SourceRemoteContentEvent;
import ws.furrify.sources.source.SourceRemoteContentQueryRepository;
import ws.furrify.sources.source.SourceRemoteContentUtils;
import ws.furrify.sources.source.dto.query.SourceDetailsQueryDTO;
import ws.furrify.sources.source.strategy.SourceArtistContentFetcher;
import ws.furrify.sources.source.strategy.SourceStrategy;
import ws.furrify.sources.vo.RemoteContent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log
final class HandleRefreshRequestImpl implements HandleRefreshRequest {

    private final RefreshRequestFactory refreshRequestFactory;

    private final DomainEventPublisher<SourceRemoteContentEvent> sourceRemoteContentEventPublisher;
    private final DomainEventPublisher<RefreshRequestEvent> refreshRequestEventPublisher;
    private final DomainEventPublisher<NewContentNotificationEvent> notificationEventPublisher;

    private final SourceQueryRepository sourceQueryRepository;
    private final SourceRemoteContentQueryRepository sourceRemoteContentQueryRepository;

    private final CacheManager cacheManager;

    @Override
    public void handleRefreshRequest(@NonNull final RefreshRequestDTO refreshRequestDTO) {
        // Fetch all sources that will need content check refresh
        List<SourceDetailsQueryDTO> sourceList = sourceQueryRepository.findAllByOwnerIdAndArtistId(
                refreshRequestDTO.getOwnerId(),
                refreshRequestDTO.getArtistId()
        );

        // Sanity check
        if (sourceList.isEmpty()) {
            // Mark as Failure if somehow no sources to check
            sendRefreshStatusChangeEvent(refreshRequestDTO, RefreshRequestStatus.FAILED);

            return;
        }

        // For each source in artist
        for (final SourceDetailsQueryDTO sourceDTO : sourceList) {
            SourceStrategy strategy = sourceDTO.getStrategy();

            // Cast strategy to fetching interface
            SourceArtistContentFetcher sourceArtistContentFetcher =
                    (SourceArtistContentFetcher) strategy;
            // Sanity check
            if (sourceArtistContentFetcher == null) {
                sendRefreshStatusChangeEvent(refreshRequestDTO, RefreshRequestStatus.FAILED);

                return;
            }

            Set<RemoteContent> fetchedArtistContentSet;

            // Fetch remote content from provider API
            try {
                fetchedArtistContentSet = sourceArtistContentFetcher.fetchArtistContent(
                        sourceDTO.getData(),
                        refreshRequestDTO.getBearerToken(),
                        cacheManager
                );
            } catch (RuntimeException e) {
                sendRefreshStatusChangeEvent(refreshRequestDTO, RefreshRequestStatus.FAILED);

                return;
            }

            // Fetch current artist remote content saved in database
            Set<RemoteContent> currentSourceRemoteContentSet =
                    sourceRemoteContentQueryRepository.findAllByOwnerIdAndSourceId(
                            refreshRequestDTO.getOwnerId(),
                            sourceDTO.getSourceId()
                    ).stream().collect(Collectors.toUnmodifiableSet());

            // Check if any new content is needed to be saved in database
            if (!currentSourceRemoteContentSet.containsAll(fetchedArtistContentSet)) {
                sendUpdateSourceRemoteContentEvent(
                        sourceDTO,
                        fetchedArtistContentSet
                );
            }

            // Remove current content fetched from source to leave only new unique content
            HashSet<RemoteContent> uniqueNewContent = new HashSet<>(fetchedArtistContentSet);
            uniqueNewContent.removeAll(currentSourceRemoteContentSet);

            // Send new content notifications to users
            sendNewContentNotificationEvent(
                    sourceDTO.getSourceId(),
                    refreshRequestDTO.getOwnerId(),
                    uniqueNewContent.stream().toList()
            );
        }

        sendRefreshStatusChangeEvent(refreshRequestDTO, RefreshRequestStatus.COMPLETED);
    }

    private void sendNewContentNotificationEvent(final UUID sourceId,
                                                 final UUID ownerId,
                                                 final List<RemoteContent> newRemoteContent) {
        notificationEventPublisher.publish(
                DomainEventPublisher.Topic.NEW_CONTENT_NOTIFICATION,
                // Use ownerId as key
                ownerId,
                NewContentNotificationUtils.createNewContentNotificationEvent(
                        DomainEventPublisher.NewContentNotificationEventType.CREATED,
                        NewContentNotificationDTO.builder()
                                .notificationId(null)
                                .sourceId(sourceId)
                                .ownerId(ownerId)
                                .newRemoteContentList(newRemoteContent)
                                .viewed(false)
                                .createDate(null)
                                .build()
                )
        );
    }

    private void sendUpdateSourceRemoteContentEvent(final SourceDetailsQueryDTO sourceDetailsQueryDTO,
                                                    final Set<RemoteContent> remoteContentSet) {

        // Publish refresh request event
        sourceRemoteContentEventPublisher.publish(
                DomainEventPublisher.Topic.SOURCE_REMOTE_CONTENT,
                // Use ownerId as key
                sourceDetailsQueryDTO.getOwnerId(),
                SourceRemoteContentUtils.createSourceRemoteContentEvent(
                        DomainEventPublisher.SourceRemoteContentEventType.REPLACED,
                        sourceDetailsQueryDTO.getOwnerId(),
                        sourceDetailsQueryDTO.getSourceId(),
                        remoteContentSet
                )
        );
    }

    private void sendRefreshStatusChangeEvent(final RefreshRequestDTO refreshRequestDTO,
                                              final RefreshRequestStatus status) {
        RefreshRequest refreshRequest = refreshRequestFactory.from(
                refreshRequestDTO.toBuilder()
                        .status(status)
                        .build()
        );

        // Publish refresh request event
        refreshRequestEventPublisher.publish(
                DomainEventPublisher.Topic.REFRESH_REQUEST,
                // Use ownerId as key
                refreshRequestDTO.getOwnerId(),
                RefreshRequestUtils.createRefreshRequestEvent(
                        DomainEventPublisher.RefreshRequestEventType.UPDATED,
                        refreshRequest
                )
        );
    }
}
