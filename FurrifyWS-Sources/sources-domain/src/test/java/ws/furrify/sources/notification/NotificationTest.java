package ws.furrify.sources.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.sources.vo.RemoteContent;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationTest {

    private NewContentNotificationSnapshot newContentNotificationSnapshot;
    private NewContentNotification newContentNotification;

    @BeforeEach
    void setUp() throws URISyntaxException {
        newContentNotificationSnapshot = NewContentNotificationSnapshot.builder()
                .id(0L)
                .notificationId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .sourceId(UUID.randomUUID())
                .newRemoteContentList(List.of(
                        new RemoteContent(null, "sad21-21dsa", new URI("https://example.com/sad21-21dsa"))
                ))
                .viewed(false)
                .newRemoteContentList(new ArrayList<>())
                .createDate(ZonedDateTime.now())
                .build();

        newContentNotification = NewContentNotification.restore(newContentNotificationSnapshot);
    }

    @Test
    @DisplayName("NewContentNotification restore from snapshot")
    void restore() {
        // Given newContentNotificationSnapshot
        // When restore() method called
        NewContentNotification newContentNotification = NewContentNotification.restore(newContentNotificationSnapshot);
        // Then restore all values into aggregate and back to snapshot without losing data
        assertEquals(newContentNotificationSnapshot, newContentNotification.getSnapshot(), "Data was lost in restore call.");
    }

    @Test
    @DisplayName("Get snapshot from newContentNotification")
    void getSnapshot() {
        // Given newContentNotification
        // When getSnapshot() method called
        NewContentNotificationSnapshot newContentNotificationSnapshot = newContentNotification.getSnapshot();
        // Then get snapshot of current data in aggregate
        assertEquals(this.newContentNotificationSnapshot, newContentNotificationSnapshot, "Data was lost in snapshot.");
    }

    @Test
    @DisplayName("Mark as viewed")
    void markAsViewed() {
        // Given
        // When markAsViewed() method called
        newContentNotification.markAsViewed();
        // Then viewed is set true
        var snapshot = newContentNotification.getSnapshot();

        assertTrue(snapshot.isViewed(), "Notification not is marked as viewed.");
    }

    @Test
    @DisplayName("Mark as viewed with already viewed notification")
    void markAsViewed2() throws URISyntaxException {
        // Given with already viewed notification
        newContentNotificationSnapshot = NewContentNotificationSnapshot.builder()
                .id(0L)
                .notificationId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .sourceId(UUID.randomUUID())
                .viewed(true)
                .newRemoteContentList(List.of(
                        new RemoteContent(null, "sad21-21dsa", new URI("https://example.com/sad21-21dsa"))
                ))
                .createDate(ZonedDateTime.now())
                .build();

        newContentNotification = NewContentNotification.restore(newContentNotificationSnapshot);
        // When markAsViewed() method called
        // Then change should be blocked
        assertThrows(IllegalStateException.class, () -> newContentNotification.markAsViewed(), "Viewed status was changed but status change backwards should be blocked.");
    }

}