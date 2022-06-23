package ws.furrify.sources.source;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.artists.ArtistServiceClient;
import ws.furrify.sources.artists.dto.query.ArtistDetailsQueryDTO;
import ws.furrify.sources.keycloak.PropertyHolder;
import ws.furrify.sources.posts.PostServiceClient;
import ws.furrify.sources.posts.dto.query.AttachmentDetailsQueryDTO;
import ws.furrify.sources.posts.dto.query.MediaDetailsQueryDTO;
import ws.furrify.sources.source.converter.SourceStrategyAttributeConverter;
import ws.furrify.sources.source.dto.SourceDTO;
import ws.furrify.sources.source.dto.SourceDtoFactory;
import ws.furrify.sources.source.strategy.SourceStrategy;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SourceFacadeTest {

    private static SourceRepository sourceRepository;
    private static SourceFacade sourceFacade;
    private static PostServiceClient postServiceClient;
    private static ArtistServiceClient artistServiceClient;

    private SourceDTO sourceDTO;
    private Source source;
    private SourceSnapshot sourceSnapshot;

    @BeforeEach
    void setUp() {
        PropertyHolder.AUTH_SERVER = "test";

        sourceDTO = SourceDTO.builder()
                .ownerId(UUID.randomUUID())
                .originId(UUID.randomUUID())
                .postId(UUID.randomUUID())
                .originType(SourceOriginType.MEDIA)
                .strategy(new SourceStrategy() {
                    @Override
                    public ValidationResult validateMedia(final HashMap<String, String> data) {
                        return ValidationResult.valid(data);
                    }

                    @Override
                    public ValidationResult validateArtist(final HashMap<String, String> data) {
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

        source = new SourceFactory().from(sourceDTO);
        sourceSnapshot = source.getSnapshot();

    }

    @BeforeAll
    static void beforeAll() {
        postServiceClient = mock(PostServiceClient.class);
        artistServiceClient = mock(ArtistServiceClient.class);
        sourceRepository = mock(SourceRepository.class);

        var sourceQueryRepository = mock(SourceQueryRepository.class);

        var sourceStrategyAttributeConverter = new SourceStrategyAttributeConverter();

        var sourceFactory = new SourceFactory();
        var sourceDTOFactory = new SourceDtoFactory(sourceQueryRepository, sourceStrategyAttributeConverter);
        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<SourceEvent>) mock(DomainEventPublisher.class);

        sourceFacade = new SourceFacade(
                new CreateSourceImpl(
                        sourceFactory,
                        eventPublisher,
                        sourceStrategyAttributeConverter,
                        postServiceClient,
                        artistServiceClient
                ),
                new DeleteSourceImpl(sourceRepository, eventPublisher),
                new UpdateSourceImpl(sourceRepository, eventPublisher, sourceStrategyAttributeConverter),
                new ReplaceSourceImpl(sourceRepository, eventPublisher, sourceStrategyAttributeConverter),
                sourceRepository,
                sourceFactory,
                sourceDTOFactory
        );
    }

    @Test
    @DisplayName("Create source for artist")
    void createArtistSource() {
        // Given userId, originId, originType and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        sourceDTO = sourceDTO.toBuilder()
                .originType(SourceOriginType.ARTIST)
                .build();
        // When createSource() method called
        ArtistDetailsQueryDTO artistDetailsQueryDTO = new ArtistDetailsQueryDTO(sourceDTO.getOriginId());

        when(artistServiceClient.getUserArtist(userId, sourceSnapshot.getOriginId())).thenReturn(artistDetailsQueryDTO);
        // Then create source and return generated uuid
        assertNotNull(sourceFacade.createSource(userId, sourceDTO), "SourceId was not returned.");
    }

    @Test
    @DisplayName("Create source for non existing artist")
    void createArtistSource2() {
        // Given userId, non-existing originId, originType and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        sourceDTO = sourceDTO.toBuilder()
                .originType(SourceOriginType.ARTIST)
                .build();
        // When createSource() method called
        when(artistServiceClient.getUserArtist(userId, sourceSnapshot.getOriginId())).thenReturn(null);
        // Then throw exception
        assertThrows(RecordNotFoundException.class,
                () -> sourceFacade.replaceSource(userId, sourceSnapshot.getSourceId(), sourceDTO),
                "Exception was not thrown.");
    }

    @Test
    @DisplayName("Create source for media")
    void createMediaSource() {
        // Given userId, originId, originType and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        sourceDTO = sourceDTO.toBuilder()
                .originType(SourceOriginType.MEDIA)
                .build();
        // When createSource() method called
        MediaDetailsQueryDTO mediaDetailsQueryDTO = new MediaDetailsQueryDTO(sourceDTO.getOriginId());

        when(postServiceClient.getPostMedia(userId, sourceSnapshot.getPostId(), sourceSnapshot.getOriginId())).thenReturn(mediaDetailsQueryDTO);
        // Then create source and return generated uuid
        assertNotNull(sourceFacade.createSource(userId, sourceDTO), "SourceId was not returned.");
    }

    @Test
    @DisplayName("Create source for non existing media")
    void createMediaSource2() {
        // Given userId, non-existing originId, originType and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        sourceDTO = sourceDTO.toBuilder()
                .originType(SourceOriginType.MEDIA)
                .build();
        // When createSource() method called
        when(postServiceClient.getPostMedia(userId, sourceSnapshot.getPostId(), sourceSnapshot.getOriginId())).thenReturn(null);
        // Then throw exception
        assertThrows(RecordNotFoundException.class,
                () -> sourceFacade.replaceSource(userId, sourceSnapshot.getSourceId(), sourceDTO),
                "Exception was not thrown.");
    }

    @Test
    @DisplayName("Create source for attachment")
    void createAttachmentSource() {
        // Given userId, originId, originType and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        sourceDTO = sourceDTO.toBuilder()
                .originType(SourceOriginType.ATTACHMENT)
                .build();
        // When createSource() method called
        AttachmentDetailsQueryDTO attachmentDetailsQueryDTO = new AttachmentDetailsQueryDTO(sourceDTO.getOriginId());

        when(postServiceClient.getPostAttachment(userId, sourceSnapshot.getPostId(), sourceSnapshot.getOriginId())).thenReturn(attachmentDetailsQueryDTO);
        // Then create source and return generated uuid
        assertNotNull(sourceFacade.createSource(userId, sourceDTO), "SourceId was not returned.");
    }

    @Test
    @DisplayName("Create source for non existing attachment")
    void createAttachmentSource2() {
        // Given userId, non-existing originId, originType and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        sourceDTO = sourceDTO.toBuilder()
                .originType(SourceOriginType.ATTACHMENT)
                .build();
        // When createSource() method called
        when(postServiceClient.getPostAttachment(userId, sourceSnapshot.getPostId(), sourceSnapshot.getOriginId())).thenReturn(null);
        // Then throw exception
        assertThrows(RecordNotFoundException.class,
                () -> sourceFacade.replaceSource(userId, sourceSnapshot.getSourceId(), sourceDTO),
                "Exception was not thrown.");
    }

    @Test
    @DisplayName("Replace source")
    void replaceSource() {
        // Given existing userId and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        // When replaceSource() method called
        when(sourceRepository.findByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(Optional.of(source));
        // Then
        assertDoesNotThrow(() -> sourceFacade.replaceSource(userId, sourceSnapshot.getSourceId(), sourceDTO),
                "Exception was thrown.");
    }

    @Test
    @DisplayName("Replace source with non existing sourceId")
    void replaceSource2() {
        // Given non existing userId and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        // When replaceSource() method called
        when(sourceRepository.findByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class,
                () -> sourceFacade.replaceSource(userId, sourceSnapshot.getSourceId(), sourceDTO),
                "Exception was not thrown.");
    }

    @Test
    @DisplayName("Update source")
    void updateSource() {
        // Given existing userId and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        // When updateSource() method called
        when(sourceRepository.findByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(Optional.of(source));
        // Then
        assertDoesNotThrow(() -> sourceFacade.updateSource(userId, sourceSnapshot.getSourceId(), sourceDTO),
                "Exception was thrown.");
    }

    @Test
    @DisplayName("Update source with non existing sourceId")
    void updateSource2() {
        // Given non existing userId and sourceDTO
        UUID userId = sourceSnapshot.getOwnerId();
        // When updateSource() method called
        when(sourceRepository.findByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class,
                () -> sourceFacade.updateSource(userId, UUID.randomUUID(), sourceDTO),
                "Exception was not thrown.");
    }


    @Test
    @DisplayName("Delete source")
    void deleteSource() {
        // Given sourceId
        UUID userId = sourceSnapshot.getOwnerId();
        // When deleteSource() method called
        when(sourceRepository.findByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(Optional.of(source));
        // Then
        assertDoesNotThrow(() -> sourceFacade.deleteSource(userId, sourceSnapshot.getSourceId()),
                "Exception was thrown.");
    }

    @Test
    @DisplayName("Delete source with non existing sourceId")
    void deleteSource2() {
        // Given sourceId
        UUID userId = sourceSnapshot.getOwnerId();
        // When deleteSource() method called
        when(sourceRepository.findByOwnerIdAndSourceId(userId, sourceSnapshot.getSourceId())).thenReturn(Optional.empty());
        // Then
        assertThrows(RecordNotFoundException.class,
                () -> sourceFacade.deleteSource(userId, sourceSnapshot.getSourceId()),
                "Exception was not thrown.");
    }
}