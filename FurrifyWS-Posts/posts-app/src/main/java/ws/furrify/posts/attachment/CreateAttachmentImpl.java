package ws.furrify.posts.attachment;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import ws.furrify.posts.attachment.dto.AttachmentDTO;
import ws.furrify.posts.attachment.strategy.AttachmentUploadStrategy;
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
final class CreateAttachmentImpl implements CreateAttachment {

    private final PostServiceClient postService;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentFactory attachmentFactory;
    private final AttachmentUploadStrategy attachmentUploadStrategy;
    private final DomainEventPublisher<AttachmentEvent> domainEventPublisher;

    @Override
    public UUID createAttachment(@NonNull final UUID userId,
                                 @NonNull final UUID postId,
                                 @NonNull final AttachmentDTO attachmentDTO,
                                 @NonNull final MultipartFile attachmentFile) {
        if (postService.getUserPost(userId, postId) == null) {
            throw new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(postId.toString()));
        }

        // Generate attachment uuid
        UUID attachmentId = UUID.randomUUID();

        // Check if file is matching declared extension
        boolean isFileContentValid = AttachmentExtension.isFileContentValid(
                attachmentFile.getOriginalFilename(),
                attachmentFile,
                attachmentDTO.getExtension()
        );
        if (!isFileContentValid) {
            throw new FileExtensionIsNotMatchingContentException(Errors.FILE_EXTENSION_IS_NOT_MATCHING_CONTENT.getErrorMessage());
        }

        // Check if filename is valid
        boolean isFilenameValid = AttachmentExtension.isFilenameValid(
                attachmentFile.getOriginalFilename()
        );
        if (!isFilenameValid) {
            throw new FilenameIsInvalidException(Errors.FILENAME_IS_INVALID.getErrorMessage(attachmentFile.getOriginalFilename()));
        }

        String md5;
        try {
            // Get file hash
            md5 = DigestUtils.md5Hex(attachmentFile.getInputStream());
        } catch (IOException e) {
            throw new FileContentIsCorruptedException(Errors.FILE_CONTENT_IS_CORRUPTED.getErrorMessage());
        }

        // Check if this post already contains attachment with md5 of file in this request
        Optional<Attachment> duplicateAttachment = attachmentRepository.findByOwnerIdAndPostIdAndMd5(userId, postId, md5);
        if (duplicateAttachment.isPresent()) {
            AttachmentSnapshot duplicateAttachmentSnapshot = duplicateAttachment.get().getSnapshot();

            throw new RecordAlreadyExistsException(Errors.FILE_HASH_DUPLICATE_IN_POST.getErrorMessage(
                    md5,
                    duplicateAttachmentSnapshot.getPostId()
            ));
        }

        // Upload file and generate thumbnail
        AttachmentUploadStrategy.UploadedAttachmentFile uploadedAttachmentFile =
                attachmentUploadStrategy.uploadAttachment(attachmentId, attachmentFile);

        // Edit attachmentDTO with generated attachment uuid
        AttachmentDTO updatedAttachmentToCreateDTO = attachmentDTO.toBuilder()
                .attachmentId(attachmentId)
                .postId(postId)
                .ownerId(userId)
                .filename(attachmentFile.getOriginalFilename())
                .fileUri(uploadedAttachmentFile.getFileUri())
                .md5(md5)
                .createDate(ZonedDateTime.now())
                .build();

        // Publish create attachment event
        domainEventPublisher.publish(
                DomainEventPublisher.Topic.ATTACHMENT,
                // User userId as key
                userId,
                AttachmentUtils.createAttachmentEvent(
                        DomainEventPublisher.AttachmentEventType.CREATED,
                        attachmentFactory.from(updatedAttachmentToCreateDTO)
                )
        );

        return attachmentId;
    }
}