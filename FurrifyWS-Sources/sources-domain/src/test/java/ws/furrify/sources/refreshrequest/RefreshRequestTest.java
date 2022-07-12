package ws.furrify.sources.refreshrequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.sources.refreshrequest.vo.RefreshRequestStatus;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RefreshRequestTest {

    private RefreshRequestSnapshot refreshRequestSnapshot;
    private RefreshRequest refreshRequest;

    @BeforeEach
    void setUp() throws URISyntaxException {
        refreshRequestSnapshot = RefreshRequestSnapshot.builder()
                .id(0L)
                .refreshRequestId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .artistId(UUID.randomUUID())
                .status(RefreshRequestStatus.PENDING)
                .bearerToken("Bearer test")
                .createDate(ZonedDateTime.now())
                .build();

        refreshRequest = RefreshRequest.restore(refreshRequestSnapshot);
    }

    @Test
    @DisplayName("RefreshRequest restore from snapshot")
    void restore() {
        // Given refreshRequestSnapshot
        // When restore() method called
        RefreshRequest refreshRequest = RefreshRequest.restore(refreshRequestSnapshot);
        // Then restore all values into aggregate and back to snapshot without losing data
        assertEquals(refreshRequestSnapshot, refreshRequest.getSnapshot(), "Data was lost in restore call.");
    }

    @Test
    @DisplayName("Get snapshot from refreshRequest")
    void getSnapshot() {
        // Given refreshRequest
        // When getSnapshot() method called
        RefreshRequestSnapshot refreshRequestSnapshot = refreshRequest.getSnapshot();
        // Then get snapshot of current data in aggregate
        assertEquals(this.refreshRequestSnapshot, refreshRequestSnapshot, "Data was lost in snapshot.");
    }

    @Test
    @DisplayName("Mark as IN_PROGRESS")
    void markInProgress() {
        // Given
        // When markInProgress() method called
        refreshRequest.markInProgress();
        // Then status is set to IN_PROGRESS
        var snapshot = refreshRequest.getSnapshot();

        assertEquals(RefreshRequestStatus.IN_PROGRESS, snapshot.getStatus(), "Status has not been changed.");
    }

    @Test
    @DisplayName("Mark as IN_PROGRESS with current status different than PENDING")
    void markInProgress2() {
        // Given
        refreshRequestSnapshot = RefreshRequestSnapshot.builder()
                .id(0L)
                .refreshRequestId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .artistId(UUID.randomUUID())
                .status(RefreshRequestStatus.FAILED)
                .bearerToken("Bearer test")
                .createDate(ZonedDateTime.now())
                .build();

        refreshRequest = RefreshRequest.restore(refreshRequestSnapshot);
        // When markInProgress() method called
        // Then change should be blocked
        assertThrows(IllegalStateException.class, () -> refreshRequest.markInProgress(), "Status was changed but status change backwards should be blocked.");
    }

    @Test
    @DisplayName("Mark as FAILED")
    void markFailed() {
        // Given
        refreshRequestSnapshot = RefreshRequestSnapshot.builder()
                .id(0L)
                .refreshRequestId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .artistId(UUID.randomUUID())
                .status(RefreshRequestStatus.IN_PROGRESS)
                .bearerToken("Bearer test")
                .createDate(ZonedDateTime.now())
                .build();

        refreshRequest = RefreshRequest.restore(refreshRequestSnapshot);
        // When markFailed() method called
        refreshRequest.markFailed();
        // Then status is set to FAILED
        var snapshot = refreshRequest.getSnapshot();

        assertEquals(RefreshRequestStatus.FAILED, snapshot.getStatus(), "Status has not been changed.");
    }

    @Test
    @DisplayName("Mark as FAILED with current status different than IN_PROGRESS")
    void markFailed2() {
        // Given
        // When markFailed() method called
        // Then change should be blocked
        assertThrows(IllegalStateException.class, () -> refreshRequest.markFailed(), "Status was changed but status change backwards should be blocked.");
    }


    @Test
    @DisplayName("Mark as COMPLETED")
    void markCompleted() {
        // Given
        refreshRequestSnapshot = RefreshRequestSnapshot.builder()
                .id(0L)
                .refreshRequestId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .artistId(UUID.randomUUID())
                .status(RefreshRequestStatus.IN_PROGRESS)
                .bearerToken("Bearer test")
                .createDate(ZonedDateTime.now())
                .build();

        refreshRequest = RefreshRequest.restore(refreshRequestSnapshot);
        // When markCompleted() method called
        refreshRequest.markCompleted();
        // Then status is set to COMPLETED
        var snapshot = refreshRequest.getSnapshot();

        assertEquals(RefreshRequestStatus.COMPLETED, snapshot.getStatus(), "Status has not been changed.");
    }

    @Test
    @DisplayName("Mark as COMPLETED with current status different than IN_PROGRESS")
    void markCompleted2() {
        // Given
        // When markFailed() method called
        // Then change should be blocked
        assertThrows(IllegalStateException.class, () -> refreshRequest.markCompleted(), "Status was changed but status change backwards should be blocked.");
    }

}