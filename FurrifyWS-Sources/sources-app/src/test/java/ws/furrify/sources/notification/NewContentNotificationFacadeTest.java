package ws.furrify.sources.notification;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.sources.notification.dto.NewContentNotificationDTO;
import ws.furrify.sources.notification.dto.NewContentNotificationDtoFactory;
import ws.furrify.sources.vo.RemoteContent;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NewContentNotificationFacadeTest {

    private static NewContentNotificationRepository notificationRepository;
    private static NewContentNotificationFacade notificationFacade;

    private NewContentNotificationDTO notificationDTO;
    private NewContentNotification notification;
    private NewContentNotificationSnapshot notificationSnapshot;

    @BeforeAll
    static void beforeAll() {
        notificationRepository = mock(NewContentNotificationRepository.class);

        var notificationQueryRepository = mock(NewContentNotificationQueryRepository.class);

        var notificationFactory = new NotificationFactory();
        var notificationDTOFactory = new NewContentNotificationDtoFactory(notificationQueryRepository);
        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<NewContentNotificationEvent>) mock(DomainEventPublisher.class);

        notificationFacade = new NewContentNotificationFacade(
                new MarkNewContentNotificationAsViewedImpl(
                        eventPublisher,
                        notificationRepository
                ),
                notificationRepository,
                notificationDTOFactory,
                notificationFactory
        );
    }

    @BeforeEach
    void setUp() throws URISyntaxException {
        notificationDTO = NewContentNotificationDTO.builder()
                .id(0L)
                .notificationId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .sourceId(UUID.randomUUID())
                .newRemoteContentList(List.of(
                        new RemoteContent(null, "das", new URI("https://example.com"))
                ))
                .viewed(false)
                .createDate(ZonedDateTime.now())
                .build();

        notification = new NotificationFactory().from(notificationDTO);
        notificationSnapshot = notification.getSnapshot();
    }

    // TODO Make sure in ALL facades in projects that events are send when they are supposed to

    @Test
    @DisplayName("Mark new content notification as viewed")
    void markNewContentNotificationAsViewed() {
        // Given userId, sourceId, notificationId
        // When markNewContentNotificationAsViewed() method called
        when(notificationRepository.findByOwnerIdAndSourceId(notificationSnapshot.getOwnerId(), notificationSnapshot.getSourceId())).thenReturn(Optional.of(notification));
        // Then mark notification as viewed

        assertDoesNotThrow(
                () -> notificationFacade.markAsViewed(notificationSnapshot.getOwnerId(), notificationSnapshot.getSourceId(), notificationSnapshot.getNotificationId()),
                "Exception was thrown."
        );
    }

    @Test
    @DisplayName("Mark new content notification as viewed with non existing notification uuid")
    void markNewContentNotificationAsViewed2() {
        // Given userId, sourceId, non-existing notificationId
        // When markNewContentNotificationAsViewed() method called
        when(notificationRepository.findByOwnerIdAndSourceId(notificationSnapshot.getOwnerId(), notificationSnapshot.getSourceId())).thenReturn(Optional.empty());
        // Then throw no record found exception

        assertThrows(
                RecordNotFoundException.class,
                () -> notificationFacade.markAsViewed(notificationSnapshot.getOwnerId(), notificationSnapshot.getSourceId(), notificationSnapshot.getNotificationId()),
                "Exception was not thrown."
        );
    }
}