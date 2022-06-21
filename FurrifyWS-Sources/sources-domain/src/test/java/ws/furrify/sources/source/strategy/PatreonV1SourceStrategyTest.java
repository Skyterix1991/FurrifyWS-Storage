package ws.furrify.sources.source.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatreonV1SourceStrategyTest {

    private UUID id;
    private HashMap<String, String> data;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        data = new HashMap<>();
    }

    @Test
    @DisplayName("Validate media")
    void validateMedia() {
        // Given
        PatreonV1SourceStrategy patreonV1SourceStrategy = new PatreonV1SourceStrategy();
        // When
        // Then
        SourceStrategy.ValidationResult validationResult = patreonV1SourceStrategy.validateMedia(data);

        assertAll(() -> {
            assertTrue(validationResult.isValid(), "Validation failed with correct parameters.");
            assertEquals(0, validationResult.getData().size(), "Validation result data contains too many parameters.");
        });
    }

    @Test
    @DisplayName("Validate attachment")
    void validateAttachment() {
        // Given
        PatreonV1SourceStrategy patreonV1SourceStrategy = new PatreonV1SourceStrategy();
        // When
        // Then
        SourceStrategy.ValidationResult validationResult = patreonV1SourceStrategy.validateMedia(data);

        assertAll(() -> {
            assertTrue(validationResult.isValid(), "Validation failed with correct parameters.");
            assertEquals(0, validationResult.getData().size(), "Validation result data contains too many parameters.");
        });
    }

    @Test
    @DisplayName("Validate attachment")
    void validateUser() {
        // Given
        PatreonV1SourceStrategy patreonV1SourceStrategy = new PatreonV1SourceStrategy();
        // When
        // Then
        SourceStrategy.ValidationResult validationResult = patreonV1SourceStrategy.validateMedia(data);

        assertAll(() -> {
            assertTrue(validationResult.isValid(), "Validation failed with correct parameters.");
            assertEquals(0, validationResult.getData().size(), "Validation result data contains too many parameters.");
        });
    }
}