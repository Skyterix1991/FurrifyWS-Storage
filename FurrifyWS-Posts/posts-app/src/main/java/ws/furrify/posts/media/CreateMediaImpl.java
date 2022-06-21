package ws.furrify.posts.media;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.media.dto.MediaDTO;
import ws.furrify.posts.media.strategy.MediaUploadStrategy;
import ws.furrify.posts.post.dto.PostServiceClient;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.FileContentIsCorruptedException;
import ws.furrify.shared.exception.FileExtensionIsNotMatchingContentException;
import ws.furrify.shared.exception.FilenameIsInvalidException;
import ws.furrify.shared.exception.RecordAlreadyExistsException;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
final class CreateMediaImpl implements CreateMedia {

    private final PostServiceClient postService;
    private final MediaRepository mediaRepository;
    private final MediaFactory mediaFactory;
    private final MediaUploadStrategy mediaUploadStrategy;
    private final DomainEventPublisher<MediaEvent> domainEventPublisher;

    @Override
    public UUID createMedia(@NonNull final UUID userId,
                            @NonNull final UUID postId,
                            @NonNull final MediaDTO mediaDTO,
                            @NonNull final MultipartFile mediaFile,
                            final MultipartFile thumbnailFile) {
        if (postService.getUserPost(userId, postId) == null) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId.toString()));
        }

        // Generate media uuid
        UUID mediaId = UUID.randomUUID();

        // Check if file is matching declared extension
        boolean isFileContentValid = MediaExtension.isFileContentValid(
                mediaFile.getOriginalFilename(),
                mediaFile,
                mediaDTO.getExtension()
        );
        if (!isFileContentValid) {
            throw new FileExtensionIsNotMatchingContentException(Errors.FILE_EXTENSION_IS_NOT_MATCHING_CONTENT.getErrorMessage());
        }

        // Check if filename is valid
        boolean isFilenameValid = MediaExtension.isFilenameValid(
                mediaFile.getOriginalFilename()
        );
        if (!isFilenameValid) {
            throw new FilenameIsInvalidException(Errors.FILENAME_IS_INVALID.getErrorMessage(mediaFile.getOriginalFilename()));
        }

        String md5;
        try {
            // Get file hash
            md5 = DigestUtils.md5Hex(mediaFile.getInputStream());
        } catch (IOException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }

        // Check if this post already contains media with md5 of file in this request
        Optional<Media> duplicateMedia = mediaRepository.findByOwnerIdAndPostIdAndMd5(userId, postId, md5);
        if (duplicateMedia.isPresent()) {
            MediaSnapshot duplicateMediaSnapshot = duplicateMedia.get().getSnapshot();

            throw new RecordAlreadyExistsException(Errors.FILE_HASH_DUPLICATE_IN_POST.getErrorMessage(
                    md5,
                    duplicateMediaSnapshot.getPostId()
            ));
        }

        MediaUploadStrategy.UploadedMediaFile uploadedMediaFile;

        // If thumbnail is present
        if (thumbnailFile != null) {
            // Check if thumbnail meets the requirements
            boolean isThumbnailFileValid = MediaExtension.isThumbnailValid(
                    thumbnailFile.getOriginalFilename(),
                    thumbnailFile
            );
            if (!isThumbnailFileValid) {
                throw new FileExtensionIsNotMatchingContentException(Errors.THUMBNAIL_CONTENT_IS_INVALID.getErrorMessage());
            }

            // Upload media with thumbnail
            uploadedMediaFile = mediaUploadStrategy.uploadMedia(
                    mediaId,
                    mediaDTO.getExtension(),
                    mediaFile,
                    thumbnailFile
            );
        } else {
            // Generate thumbnail and upload media and thumbnail
            uploadedMediaFile = mediaUploadStrategy.uploadMediaWithGeneratedThumbnail(
                    mediaId,
                    mediaDTO.getExtension(),
                    mediaFile
            );
        }

        // Edit mediaDTO with generated media uuid
        MediaDTO updatedMediaToCreateDTO = mediaDTO.toBuilder()
                .mediaId(mediaId)
                .postId(postId)
                .ownerId(userId)
                .filename(mediaFile.getOriginalFilename())
                .fileUri(uploadedMediaFile.getFileUri())
                .thumbnailUri(uploadedMediaFile.getThumbnailUri())
                .md5(md5)
                .priority(mediaDTO.getPriority())
                .createDate(ZonedDateTime.now())
                .build();

        // Publish create media event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.MEDIA,
                // User userId as key
                userId,
                MediaUtils.createMediaEvent(
                        DomainEventPublisher.MediaEventType.CREATED,
                        mediaFactory.from(updatedMediaToCreateDTO)
                )
        );

        return mediaId;
    }
}