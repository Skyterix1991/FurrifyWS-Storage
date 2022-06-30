package ws.furrify.sources.refreshrequest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class RequestRefreshTest implements CommandLineRunner {

    private final SqlRefreshRequestRepository sqlRefreshRequestRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingRefreshRequests());
    }

    private void createTestingRefreshRequests() {
        var userId = UUID.fromString("4b255497-0200-4ee1-8922-892233173c10");
        var artistId = UUID.fromString("9551e7e0-4550-41b9-8c4a-57943642fa00");

        var requestRefreshId = UUID.fromString("717a2b2a-3887-4cb4-9d25-cc92f7382a9f");

        sqlRefreshRequestRepository.save(
                RefreshRequestSnapshot.builder()
                        .ownerId(userId)
                        .artistId(artistId)
                        .refreshRequestId(requestRefreshId)
                        .status(RefreshRequestStatus.COMPLETED)
                        .bearerToken("test")
                        .createDate(ZonedDateTime.now())
                        .build()
        );

        System.out.println("RefreshRequestId: " + requestRefreshId);
    }

}
