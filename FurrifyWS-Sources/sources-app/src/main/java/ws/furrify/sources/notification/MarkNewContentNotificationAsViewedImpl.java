package ws.furrify.sources.notification;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.util.UUID;

@RequiredArgsConstructor
@Log
final class MarkNewContentNotificationAsViewedImpl implements MarkNewContentNotificationAsViewed {

    private final DomainEventPublisher<NewContentNotificationEvent> eventPublisher;

    private final NewContentNotificationRepository notificationRepository;

    @Override
    public void markNewContentNotificationAsViewed(@NonNull final UUID ownerId,
                                                   @NonNull final UUID sourceId,
                                                   @NonNull final UUID notificationId) {
        NewContentNotification notification = notificationRepository.findByOwnerIdAndSourceId(ownerId, sourceId)
                .orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(sourceId.toString())));
        notification.markAsViewed();

        // Publish update new content notification event
        eventPublisher.publish(
                DomainEventPublisher.Topic.NEW_CONTENT_NOTIFICATION,
                // Use ownerId as key
                ownerId,
                NewContentNotificationUtils.createNewContentNotificationEvent(
                        DomainEventPublisher.NewContentNotificationEventType.UPDATED,
                        notification
                )
        );
    }
}
