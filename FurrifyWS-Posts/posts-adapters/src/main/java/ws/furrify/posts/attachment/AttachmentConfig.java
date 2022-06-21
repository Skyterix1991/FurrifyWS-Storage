package ws.furrify.posts.attachment;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.posts.attachment.dto.AttachmentDtoFactory;
import ws.furrify.posts.attachment.strategy.AttachmentUploadStrategy;
import ws.furrify.posts.attachment.strategy.LocalStorageAttachmentUploadStrategy;
import ws.furrify.posts.kafka.KafkaTopicEventPublisher;
import ws.furrify.posts.post.PostServiceImpl;

@Configuration
@RequiredArgsConstructor
class AttachmentConfig {

    private final AttachmentRepositoryImpl attachmentRepository;
    private final AttachmentQueryRepository attachmentQueryRepository;
    private final KafkaTopicEventPublisher<AttachmentEvent> eventPublisher;
    private final PostServiceImpl postServiceClient;

    @Bean
    AttachmentFacade attachmentFacade() {
        var attachmentFactory = new AttachmentFactory();
        var attachmentDtoFactory = new AttachmentDtoFactory(attachmentQueryRepository);

        return new AttachmentFacade(
                new CreateAttachmentImpl(postServiceClient, attachmentRepository, attachmentFactory, attachmentUploadStrategy(), eventPublisher),
                new DeleteAttachmentImpl(eventPublisher, attachmentRepository),
                attachmentRepository,
                attachmentFactory,
                attachmentDtoFactory
        );
    }

    @Bean
    AttachmentUploadStrategy attachmentUploadStrategy() {
        return new LocalStorageAttachmentUploadStrategy();
    }
}
