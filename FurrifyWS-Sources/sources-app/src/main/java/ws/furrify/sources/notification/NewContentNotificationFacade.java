package ws.furrify.sources.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.notification.dto.NewContentNotificationDTO;
import ws.furrify.sources.notification.dto.NewContentNotificationDtoFactory;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
final public class NewContentNotificationFacade {

    private final MarkNewContentNotificationAsViewed markNewContentNotificationAsViewed;
    private final NewContentNotificationRepository notificationRepository;
    private final NewContentNotificationDtoFactory newContentNotificationDtoFactory;

    private final NotificationFactory notificationFactory;

    /**
     * Handle incoming new content notification events.
     *
     * @param notificationEvent New content notification event instance received from kafka.
     */
    public void handleEvent(final UUID key, final NewContentNotificationEvent notificationEvent) {
        NewContentNotificationDTO newContentNotificationDTO = newContentNotificationDtoFactory.from(key, notificationEvent);

        switch (DomainEventPublisher.NewContentNotificationEventType.valueOf(notificationEvent.getState())) {
            case CREATED, UPDATED -> saveNewContentNotificationToDatabase(newContentNotificationDTO);

            default -> log.warning("State received from kafka is not defined. " +
                    "State=" + notificationEvent.getState() + " Topic=new_content_notification_events");
        }
    }

    /**
     * Mark notification as viewed by user.
     *
     * @param userId         Owner UUID.
     * @param sourceId       Source UUID.
     * @param notificationId New content notification UUID to mark as viewed
     */
    public void markAsViewed(final UUID userId, final UUID sourceId, final UUID notificationId) {
        markNewContentNotificationAsViewed.markNewContentNotificationAsViewed(userId, sourceId, notificationId);
    }

    private void saveNewContentNotificationToDatabase(final NewContentNotificationDTO newContentNotificationDTO) {
        notificationRepository.save(notificationFactory.from(newContentNotificationDTO));
    }
}
