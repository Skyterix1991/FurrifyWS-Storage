package ws.furrify.sources.source;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.keycloak.PropertyHolder;
import ws.furrify.sources.source.strategy.PatreonV1SourceStrategy;
import ws.furrify.sources.source.strategy.SourceStrategy;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class SourceTest {

    // Mocked in beforeAll()
    private static SourceRepository sourceRepository;

    private SourceSnapshot sourceSnapshot;
    private Source source;

    @BeforeEach
    void setUp() {
        PropertyHolder.AUTH_SERVER = "test";

        sourceSnapshot = SourceSnapshot.builder()
                .id(0L)
                .sourceId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .originId(UUID.randomUUID())
                .originType(SourceOriginType.ARTIST)
                .strategy(new SourceStrategy() {
                    @Override
                    public ValidationResult validateMedia(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }

                    @Override
                    public ValidationResult validateUser(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }

                    @Override
                    public ValidationResult validateAttachment(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }
                })
                .data(new HashMap<>())
                .createDate(ZonedDateTime.now())
                .build();

        source = Source.restore(sourceSnapshot);
    }

    @BeforeAll
    static void beforeAll() {
        sourceRepository = mock(SourceRepository.class);
    }

    @Test
    @DisplayName("Source restore from snapshot")
    void restore() {
        // Given sourceSnapshot
        // When restore() method called
        Source source = Source.restore(sourceSnapshot);
        // Then restore all values into aggregate and back to snapshot without losing data
        assertEquals(sourceSnapshot, source.getSnapshot(), "Data was lost in restore call.");
    }

    @Test
    @DisplayName("Get snapshot from source")
    void getSnapshot() {
        // Given source
        // When getSnapshot() method called
        SourceSnapshot sourceSnapshot = source.getSnapshot();
        // Then get snapshot of current data in aggregate
        assertEquals(this.sourceSnapshot, sourceSnapshot, "Data was lost in snapshot.");
    }

    @Test
    @DisplayName("Update data with new values for artist")
    void updateData() {
        // Given source with origin artist, request data and new mocked strategy
        HashMap<String, String> requestData = new HashMap<>() {{
            put("test", "test2");
        }};

        sourceSnapshot = SourceSnapshot.builder()
                .id(0L)
                .sourceId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .originId(UUID.randomUUID())
                .originType(SourceOriginType.ARTIST)
                .strategy(new SourceStrategy() {
                    @Override
                    public ValidationResult validateMedia(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }

                    @Override
                    public ValidationResult validateUser(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }

                    @Override
                    public ValidationResult validateAttachment(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }
                })
                .data(new HashMap<>())
                .createDate(ZonedDateTime.now())
                .build();

        // Update source with new values
        source = Source.restore(sourceSnapshot);

        // New strategy
        SourceStrategy newStrategy = new SourceStrategy() {
            @Override
            public ValidationResult validateMedia(final HashMap<String, String> data) {
                return ValidationResult.valid(requestData);
            }

            @Override
            public ValidationResult validateUser(final HashMap<String, String> data) {
                return ValidationResult.valid(requestData);
            }

            @Override
            public ValidationResult validateAttachment(final HashMap<String, String> data) {
                return ValidationResult.valid(requestData);
            }
        };

        // When updateData() method called
        // Then update all values
        source.updateData(requestData, newStrategy);

        var updatedSnapshot = source.getSnapshot();

        assertAll(
                () -> assertEquals(newStrategy, updatedSnapshot.getStrategy(), "Strategy was not updated."),
                () -> assertEquals(requestData, updatedSnapshot.getData(), "Data was not updated.")
        );
    }

    @Test
    @DisplayName("Update data with new strategy")
    void updateData2() {
        // Given new strategy
        SourceStrategy sourceStrategy = new PatreonV1SourceStrategy();
        // When updateData() method called
        // Then update strategy
        source.updateData(null, sourceStrategy);

        var snapshot = source.getSnapshot();

        assertAll(
                () -> assertEquals(sourceStrategy, snapshot.getStrategy(), "Strategy was not updated."),
                () -> assertEquals(sourceSnapshot.getData(), snapshot.getData(), "Data was not updated.")
        );
    }

    @Test
    @DisplayName("Update data with new data")
    void updateData3() {
        // Given new data
        HashMap<String, String> data = new HashMap<>() {{
            put("username", "test2");
        }};
        // When updateData() method called
        // Then update data
        source.updateData(data, null);

        var snapshot = source.getSnapshot();

        assertAll(
                () -> assertEquals(sourceSnapshot.getStrategy(), snapshot.getStrategy(), "Strategy was not updated."),
                () -> assertEquals(data, snapshot.getData(), "Data was not updated.")
        );
    }

    @Test
    @DisplayName("Update data with new values for media")
    void updateData4() {
        // Given source with origin artist, request data and new mocked strategy
        HashMap<String, String> requestData = new HashMap<>() {{
            put("test", "test2");
        }};

        sourceSnapshot = SourceSnapshot.builder()
                .id(0L)
                .sourceId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .originId(UUID.randomUUID())
                .originType(SourceOriginType.MEDIA)
                .strategy(new SourceStrategy() {
                    @Override
                    public ValidationResult validateMedia(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }

                    @Override
                    public ValidationResult validateUser(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }

                    @Override
                    public ValidationResult validateAttachment(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }
                })
                .data(new HashMap<>())
                .createDate(ZonedDateTime.now())
                .build();

        // Update source with new values
        source = Source.restore(sourceSnapshot);

        // New strategy
        SourceStrategy newStrategy = new SourceStrategy() {
            @Override
            public ValidationResult validateMedia(final HashMap<String, String> data) {
                return ValidationResult.valid(requestData);
            }

            @Override
            public ValidationResult validateUser(final HashMap<String, String> data) {
                return ValidationResult.valid(requestData);
            }

            @Override
            public ValidationResult validateAttachment(final HashMap<String, String> data) {
                return ValidationResult.valid(requestData);
            }
        };

        // When updateData() method called
        // Then update all values
        source.updateData(requestData, newStrategy);

        var updatedSnapshot = source.getSnapshot();

        assertAll(
                () -> assertEquals(newStrategy, updatedSnapshot.getStrategy(), "Strategy was not updated."),
                () -> assertEquals(requestData, updatedSnapshot.getData(), "Data was not updated.")
        );
    }

    @Test
    @DisplayName("Update data with new values for attachment")
    void updateData5() {
        // Given source with origin artist, request data and new mocked strategy
        HashMap<String, String> requestData = new HashMap<>() {{
            put("test", "test2");
        }};

        sourceSnapshot = SourceSnapshot.builder()
                .id(0L)
                .sourceId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .originId(UUID.randomUUID())
                .originType(SourceOriginType.ATTACHMENT)
                .strategy(new SourceStrategy() {
                    @Override
                    public ValidationResult validateMedia(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }

                    @Override
                    public ValidationResult validateUser(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }

                    @Override
                    public ValidationResult validateAttachment(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }
                })
                .data(new HashMap<>())
                .createDate(ZonedDateTime.now())
                .build();

        // Update source with new values
        source = Source.restore(sourceSnapshot);

        // New strategy
        SourceStrategy newStrategy = new SourceStrategy() {
            @Override
            public ValidationResult validateMedia(final HashMap<String, String> data) {
                return ValidationResult.valid(requestData);
            }

            @Override
            public ValidationResult validateUser(final HashMap<String, String> data) {
                return ValidationResult.valid(requestData);
            }

            @Override
            public ValidationResult validateAttachment(final HashMap<String, String> data) {
                return ValidationResult.valid(requestData);
            }
        };

        // When updateData() method called
        // Then update all values
        source.updateData(requestData, newStrategy);

        var updatedSnapshot = source.getSnapshot();

        assertAll(
                () -> assertEquals(newStrategy, updatedSnapshot.getStrategy(), "Strategy was not updated."),
                () -> assertEquals(requestData, updatedSnapshot.getData(), "Data was not updated.")
        );
    }

}