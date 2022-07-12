package ws.furrify.sources.notification;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ws.furrify.sources.vo.RemoteContent;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class NewContentNotificationTest implements CommandLineRunner {

    private final SqlNewContentNotificationRepository sqlNewContentNotificationRepository;
    private final Environment environment;

    @Override
    public void run(final String... args) {
        Arrays.stream(environment.getActiveProfiles())
                .filter("dev"::equals)
                .findAny()
                .ifPresent((profile) -> createTestingNewContentNotification());
    }

    @SneakyThrows
    private void createTestingNewContentNotification() {
        var userId = UUID.fromString("4b255497-0200-4ee1-8922-892233173c10");
        var sourceId = UUID.fromString("02038a77-9717-4de8-a21b-3a722f158be2");

        var newContentNotificationId = UUID.fromString("bdaa2beb-ac3f-4381-a4e7-6d92c6e279a7");

        sqlNewContentNotificationRepository.save(
                NewContentNotificationSnapshot.builder()
                        .ownerId(userId)
                        .sourceId(sourceId)
                        .notificationId(newContentNotificationId)
                        .newRemoteContentList(List.of(
                                new RemoteContent(null, "312jd-321da", new URI("https://example.com/312jd-321da"))
                        ))
                        .viewed(false)
                        .createDate(ZonedDateTime.now())
                        .build()
        );

        System.out.println("NewContentNotificationId: " + newContentNotificationId);
    }

}
