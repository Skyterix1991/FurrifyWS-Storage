package ws.furrify.posts.media;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.dto.MediaDtoFactory;
import ws.furrify.posts.media.strategy.MediaUploadStrategy;
import ws.furrify.posts.post.dto.PostServiceClient;
import ws.furrify.posts.post.dto.query.PostDetailsDTO;
import ws.furrify.shared.exception.RecordAlreadyExistsException;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MediaFacadeTest {

    private static MediaRepository mediaRepository;
    private static MediaFacade mediaFacade;
    private static MediaUploadStrategy mediaUploadStrategy;
    private static PostServiceClient postServiceClient;

    private MediaDTO mediaDTO;
    private Media media;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        mediaDTO = MediaDTO.builder()
                .ownerId(UUID.randomUUID())
                .postId(UUID.randomUUID())
                .mediaId(UUID.randomUUID())
                .priority(0)
                .extension(MediaExtension.EXTENSION_PNG)
                .filename("yes.png")
                .fileUri(new URI("/media"))
                .thumbnailUri(new URI("/media"))
                .md5("3c518eeb674c71b30297f072fde7eba5")
                .createDate(ZonedDateTime.now())
                .build();

        media = new MediaFactory().from(mediaDTO);
    }

    @BeforeAll
    static void beforeAll() {
        mediaRepository = mock(MediaRepository.class);
        var mediaQueryRepository = mock(MediaQueryRepository.class);
        mediaUploadStrategy = mock(MediaUploadStrategy.class);
        postServiceClient = mock(PostServiceClient.class);

        var mediaFactory = new MediaFactory();
        var mediaDtoFactory = new MediaDtoFactory(mediaQueryRepository);

        @SuppressWarnings("unchecked")
        var eventPublisher = (DomainEventPublisher<MediaEvent>) mock(DomainEventPublisher.class);

        mediaFacade = new MediaFacade(
                new CreateMediaImpl(postServiceClient, mediaRepository, mediaFactory, mediaUploadStrategy, eventPublisher),
                new DeleteMediaImpl(eventPublisher, mediaRepository),
                new UpdateMediaImpl(eventPublisher, mediaRepository),
                new ReplaceMediaImpl(eventPublisher, mediaRepository),
                mediaRepository,
                mediaFactory,
                mediaDtoFactory
        );
    }

    @Test
    @DisplayName("Create media with generated thumbnail")
    void createMedia() throws URISyntaxException {
        // Given ownerId, mediaDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        PostDetailsDTO postDetailsDTO = new PostDetailsDTO(null, null, null, null, null, null, null, null, null);

        MultipartFile mediaFile = new MultipartFile() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getOriginalFilename() {
                return "test.png";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return getClass().getClassLoader().getResourceAsStream("example.png");
            }

            @Override
            public void transferTo(final File file) throws IOException, IllegalStateException {

            }
        };
        // When createMedia() method called
        when(postServiceClient.getUserPost(any(), any())).thenReturn(postDetailsDTO);
        when(mediaRepository.findByOwnerIdAndPostIdAndMd5(any(), any(), any())).thenReturn(Optional.empty());
        when(mediaUploadStrategy.uploadMediaWithGeneratedThumbnail(any(), any(), any())).thenReturn(new MediaUploadStrategy.UploadedMediaFile(
                new URI("/test"),
                new URI("/test")
        ));
        // Then return generated uuid
        assertNotNull(mediaFacade.createMedia(userId, postId, mediaDTO, mediaFile, null), "MediaId was not returned.");
    }


    @Test
    @DisplayName("Create media with non existing postId")
    void createMedia2() {
        // Given ownerId, non-existing postId, mediaDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        MultipartFile mediaFile = new MultipartFile() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getOriginalFilename() {
                return "example.psd";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return getClass().getClassLoader().getResourceAsStream("example.psd");
            }

            @Override
            public void transferTo(final File file) throws IOException, IllegalStateException {

            }
        };
        // When createMedia() method called
        when(postServiceClient.getUserPost(any(), any())).thenReturn(null);
        // Then throw exception
        assertThrows(
                RecordNotFoundException.class,
                () -> mediaFacade.createMedia(userId, postId, mediaDTO, mediaFile, null),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create media with given thumbnail")
    void createMedia3() throws URISyntaxException {
        // Given ownerId, mediaDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        PostDetailsDTO postDetailsDTO = new PostDetailsDTO(null, null, null, null, null, null, null, null, null);

        MultipartFile mediaFile = new MultipartFile() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getOriginalFilename() {
                return "test.png";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() {
                return getClass().getClassLoader().getResourceAsStream("example.png");
            }

            @Override
            public void transferTo(final File file) throws IOException, IllegalStateException {

            }
        };
        MultipartFile thumbnailFile = new MultipartFile() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getOriginalFilename() {
                return "test.jpg";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 1;
            }

            @Override
            public byte[] getBytes() {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return getClass().getClassLoader().getResourceAsStream("example.jpg");
            }

            @Override
            public void transferTo(final File file) throws IOException, IllegalStateException {

            }
        };
        // When createMedia() method called
        when(postServiceClient.getUserPost(any(), any())).thenReturn(postDetailsDTO);
        when(mediaRepository.findByOwnerIdAndPostIdAndMd5(any(), any(), any())).thenReturn(Optional.empty());
        when(mediaUploadStrategy.uploadMedia(any(), any(), any(), any())).thenReturn(new MediaUploadStrategy.UploadedMediaFile(
                new URI("/test"),
                new URI("/test")
        ));
        // Then return generated uuid
        assertNotNull(mediaFacade.createMedia(userId, postId, mediaDTO, mediaFile, thumbnailFile), "MediaId was not returned.");
    }

    @Test
    @DisplayName("Create media with md5 duplicate in post")
    void createMedia4() {
        // Given ownerId, mediaDTO and multipart file
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();

        PostDetailsDTO postDetailsDTO = new PostDetailsDTO(null, null, null, null, null, null, null, null, null);

        MultipartFile mediaFile = new MultipartFile() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getOriginalFilename() {
                return "test.png";
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return getClass().getClassLoader().getResourceAsStream("example.png");
            }

            @Override
            public void transferTo(final File file) throws IOException, IllegalStateException {

            }
        };

        // When createMedia() method called
        when(postServiceClient.getUserPost(any(), any())).thenReturn(postDetailsDTO);
        when(mediaRepository.findByOwnerIdAndPostIdAndMd5(any(), any(), any())).thenReturn(Optional.of(media));
        // Then throw exception
        assertThrows(
                RecordAlreadyExistsException.class,
                () -> mediaFacade.createMedia(userId, postId, mediaDTO, mediaFile, null),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Replace media")
    void replaceMedia() {
        // Given mediaDto, userId and postId and mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When replaceMedia() method called
        when(mediaRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId))
                .thenReturn(Optional.of(media));
        // Then run successfully
        assertDoesNotThrow(() -> mediaFacade.replaceMedia(userId, postId, mediaId, mediaDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Replace media with non existing mediaId")
    void replaceMedia2() {
        // Given mediaDTO, userId, postId and non existing mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When replaceMedia() method called
        when(mediaRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId)).thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> mediaFacade.replaceMedia(userId, postId, mediaId, mediaDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Update media")
    void updateMedia() {
        // Given mediaDTO, userId, postId and mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When updateMedia() method called
        when(mediaRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId))
                .thenReturn(Optional.of(media));
        // Then run successfully
        assertDoesNotThrow(() -> mediaFacade.updateMedia(userId, postId, mediaId, mediaDTO), "Exception was thrown");
    }

    @Test
    @DisplayName("Update media with non existing mediaId")
    void updateMedia2() {
        // Given mediaDTO, userId, postId and non existing mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When updateMedia() method called
        when(mediaRepository.findByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId))
                .thenReturn(Optional.empty());
        // Then throw no record found exception
        assertThrows(
                RecordNotFoundException.class,
                () -> mediaFacade.updateMedia(userId, postId, mediaId, mediaDTO),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Delete media")
    void deleteMedia() {
        // Given userId, postId and mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When deleteMedia() method called
        when(mediaRepository.existsByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId)).thenReturn(true);
        // Then run successfully
        assertDoesNotThrow(() -> mediaFacade.deleteMedia(userId, postId, mediaId), "Exception was thrown");
    }

    @Test
    @DisplayName("Delete media with non existing mediaId")
    void deleteMedia2() {
        // Given userId, postId and non existing mediaId
        UUID userId = UUID.randomUUID();
        UUID postId = UUID.randomUUID();
        UUID mediaId = UUID.randomUUID();
        // When deleteMedia() method called
        when(mediaRepository.existsByOwnerIdAndPostIdAndMediaId(userId, postId, mediaId)).thenReturn(false);
        // Then throw record not found exception
        assertThrows(RecordNotFoundException.class, () -> mediaFacade.deleteMedia(userId, postId, mediaId), "Exception was not thrown");
    }
}
