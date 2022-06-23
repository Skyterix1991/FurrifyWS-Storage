package ws.furrify.sources.refreshrequest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.sources.feign.ArtistServiceImpl;
import ws.furrify.sources.kafka.KafkaTopicEventPublisher;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDtoFactory;
import ws.furrify.sources.source.SourceQueryRepository;

@Configuration
@RequiredArgsConstructor
class RequestRefreshConfig {

    private final RefreshRequestRepositoryImpl refreshRequestRepository;
    private final SourceQueryRepository sourceQueryRepository;
    private final RefreshRequestQueryRepository refreshRequestQueryRepository;
    private final KafkaTopicEventPublisher<RefreshRequestEvent> eventPublisher;
    private final ArtistServiceImpl artistService;

    @Bean
    RefreshRequestFacade refreshRequestFacade() {
        var refreshRequestFactory = new RefreshRequestFactory();
        var refreshRequestDtoFactory = new RefreshRequestDtoFactory(refreshRequestQueryRepository);

        return new RefreshRequestFacade(
                new CreateRefreshRequestImpl(
                        refreshRequestFactory,
                        eventPublisher,
                        artistService,
                        sourceQueryRepository
                ),
                refreshRequestRepository,
                refreshRequestFactory,
                refreshRequestDtoFactory
        );
    }

}
