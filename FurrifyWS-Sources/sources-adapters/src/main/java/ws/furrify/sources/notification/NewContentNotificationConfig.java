package ws.furrify.sources.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.sources.kafka.KafkaTopicEventPublisher;
import ws.furrify.sources.notification.dto.NewContentNotificationDtoFactory;

@Configuration
@RequiredArgsConstructor
class NewContentNotificationConfig {

    private final KafkaTopicEventPublisher<NewContentNotificationEvent> newContentNotificationEventPublisher;

    private final NewContentNotificationQueryRepository newContentNotificationQueryRepository;
    private final NewContentNotificationRepository newContentNotificationRepository;

    @Bean
    NewContentNotificationFacade newContentNotificationFacade() {
        var notificationFactory = new NotificationFactory();
        var newContentNotificationDtoFactory = new NewContentNotificationDtoFactory(newContentNotificationQueryRepository);

        return new NewContentNotificationFacade(
                new MarkNewContentNotificationAsViewedImpl(
                        newContentNotificationEventPublisher,
                        newContentNotificationRepository
                ),
                newContentNotificationRepository,
                newContentNotificationDtoFactory,
                notificationFactory
        );
    }

}
