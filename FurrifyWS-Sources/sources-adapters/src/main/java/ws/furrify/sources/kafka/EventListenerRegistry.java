package ws.furrify.sources.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import ws.furrify.sources.notification.NewContentNotificationEvent;
import ws.furrify.sources.notification.NewContentNotificationFacade;
import ws.furrify.sources.refreshrequest.RefreshRequestEvent;
import ws.furrify.sources.refreshrequest.RefreshRequestFacade;
import ws.furrify.sources.source.SourceEvent;
import ws.furrify.sources.source.SourceFacade;
import ws.furrify.sources.source.SourceRemoteContentEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log
class EventListenerRegistry {
    private final SourceFacade sourceFacade;
    private final RefreshRequestFacade refreshRequestFacade;
    private final NewContentNotificationFacade notificationFacade;

    @KafkaListener(topics = "source_events")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 10_000)
    )
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload SourceEvent sourceEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        sourceFacade.handleEvent(UUID.fromString(key), sourceEvent);
    }

    @KafkaListener(topics = "source_remote_content_events")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 10_000)
    )
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload SourceRemoteContentEvent sourceRemoteContentEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        sourceFacade.handleEvent(UUID.fromString(key), sourceRemoteContentEvent);
    }

    @KafkaListener(topics = "refresh_request_events")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 10_000)
    )
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload RefreshRequestEvent refreshRequestEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        refreshRequestFacade.handleEvent(UUID.fromString(key), refreshRequestEvent);
    }

    /**
     * Refresh request that should only be handled by one free microservice.
     * Group id will be the same across all source microservices to achieve that.
     */
    @KafkaListener(topics = "refresh_request_events",
            groupId = "${spring.kafka.consumer.group-id.prefix}worker-refresh-requests")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 10_000)
    )
    public void onRefreshRequest(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                                 @Payload RefreshRequestEvent refreshRequestEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        refreshRequestFacade.handleRefreshRequestWorkerEvent(UUID.fromString(key), refreshRequestEvent);
    }


    @KafkaListener(topics = "new_content_notification_events")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 10_000)
    )
    public void on(@Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                   @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
                   @Payload NewContentNotificationEvent notificationEvent) {
        log.info("Event received from kafka [topic=" + topic + "] [partition=" + partition + "].");

        notificationFacade.handleEvent(UUID.fromString(key), notificationEvent);
    }

}
