package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.sources.feign.ArtistServiceImpl;
import ws.furrify.sources.kafka.KafkaTopicEventPublisher;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;
import ws.furrify.sources.source.dto.SourceDtoFactory;

@Configuration
@RequiredArgsConstructor
class SourceConfig {

    private final SourceRepositoryImpl sourceRepository;
    private final SourceQueryRepository sourceQueryRepository;
    private final KafkaTopicEventPublisher<SourceEvent> eventPublisher;
    private final SourceStrategyAttributeConverter sourceStrategyAttributeConverter;
    private final ArtistServiceImpl artistService;
    private final PostServiceImpl postService;

    @Bean
    SourceFacade sourceFacade() {
        var sourceFactory = new SourceFactory();
        var sourceDtoFactory = new SourceDtoFactory(sourceQueryRepository, sourceStrategyAttributeConverter);

        return new SourceFacade(
                new CreateSourceImpl(
                        sourceFactory,
                        eventPublisher,
                        sourceStrategyAttributeConverter,
                        postService,
                        artistService
                ),
                new DeleteSourceImpl(sourceRepository, eventPublisher),
                new UpdateSourceImpl(sourceRepository, eventPublisher, sourceStrategyAttributeConverter),
                new ReplaceSourceImpl(sourceRepository, eventPublisher, sourceStrategyAttributeConverter),
                sourceRepository,
                sourceFactory,
                sourceDtoFactory
        );
    }

}
