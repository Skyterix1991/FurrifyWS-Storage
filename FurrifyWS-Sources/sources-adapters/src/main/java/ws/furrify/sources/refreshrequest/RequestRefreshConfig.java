package ws.furrify.sources.refreshrequest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ws.furrify.shared.cache.CacheManager;
import ws.furrify.sources.feign.ArtistServiceImpl;
import ws.furrify.sources.kafka.KafkaTopicEventPublisher;
import ws.furrify.sources.refreshrequest.dto.RefreshRequestDtoFactory;
import ws.furrify.sources.source.SourceQueryRepository;
import ws.furrify.sources.source.SourceRemoteContentEvent;
import ws.furrify.sources.source.SourceRemoteContentQueryRepository;

@Configuration
@RequiredArgsConstructor
class RequestRefreshConfig {

    private final RefreshRequestRepository refreshRequestRepository;
    private final SourceQueryRepository sourceQueryRepository;
    private final SourceRemoteContentQueryRepository sourceRemoteContentQueryRepository;
    private final RefreshRequestQueryRepository refreshRequestQueryRepository;
    private final KafkaTopicEventPublisher<RefreshRequestEvent> refreshRequestEventPublisher;
    private final KafkaTopicEventPublisher<SourceRemoteContentEvent> sourceRemoteContentEventPublisher;
    private final ArtistServiceImpl artistService;

    private final CacheManager cacheManager;

    @Bean
    RefreshRequestFacade refreshRequestFacade() {
        var refreshRequestFactory = new RefreshRequestFactory();
        var refreshRequestDtoFactory = new RefreshRequestDtoFactory(refreshRequestQueryRepository);

        return new RefreshRequestFacade(
                new CreateRefreshRequestImpl(
                        refreshRequestFactory,
                        refreshRequestEventPublisher,
                        artistService,
                        sourceQueryRepository
                ),
                new HandleRefreshRequestImpl(
                        refreshRequestFactory,
                        sourceRemoteContentEventPublisher,
                        refreshRequestEventPublisher,
                        sourceQueryRepository,
                        sourceRemoteContentQueryRepository,
                        cacheManager
                ),
                refreshRequestRepository,
                refreshRequestDtoFactory,
                refreshRequestFactory
        );
    }

}
