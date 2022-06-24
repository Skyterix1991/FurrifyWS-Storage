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
import ws.furrify.sources.refreshrequest.RefreshRequestEvent;
import ws.furrify.sources.refreshrequest.RefreshRequestFacade;
import ws.furrify.sources.source.SourceEvent;
import ws.furrify.sources.source.SourceFacade;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log
class EventListenerRegistry {
    private final SourceFacade sourceFacade;
    private final RefreshRequestFacade refreshRequestFacade;

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

}
