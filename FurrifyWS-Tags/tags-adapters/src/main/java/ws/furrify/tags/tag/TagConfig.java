package ws.furrify.tags.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.tags.kafka.KafkaTopicEventPublisher;
import ws.furrify.tags.tag.dto.TagDtoFactory;

@Configuration
@RequiredArgsConstructor
class TagConfig {

    private final TagQueryRepository tagQueryRepository;
    private final TagRepositoryImpl tagRepository;
    private final KafkaTopicEventPublisher<TagEvent> eventPublisher;

    @Bean
    TagFacade tagFacade() {
        var tagFactory = new TagFactory();
        var tagDtoFactory = new TagDtoFactory(tagQueryRepository);

        return new TagFacade(
                new CreateTagImpl(tagFactory, eventPublisher, tagRepository),
                new DeleteTagImpl(eventPublisher, tagRepository),
                new UpdateTagImpl(eventPublisher, tagRepository),
                new ReplaceTagImpl(eventPublisher, tagRepository),
                tagRepository,
                tagFactory,
                tagDtoFactory
        );
    }
}
