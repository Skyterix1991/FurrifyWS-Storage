package ws.furrify.posts.attachment;

import ws.furrify.posts.attachment.vo.AttachmentData;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.Instant;
import java.util.UUID;

/**
 * Utils class regarding Attachment entity.
 *
 * @author Skyte
 */
class AttachmentUtils {

    /**
     * Create AttachmentEvent by given aggregate and set its state change.
     *
     * @param eventType  Event state change type.
     * @param attachment Attachment aggregate to build post event from.
     * @return Created attachment event.
     */
    public static AttachmentEvent createAttachmentEvent(final DomainEventPublisher.AttachmentEventType eventType,
                                                        final Attachment attachment) {
        AttachmentSnapshot attachmentSnapshot = attachment.getSnapshot();

        return AttachmentEvent.newBuilder()
                .setState(eventType.name())
                .setAttachmentId(attachmentSnapshot.getAttachmentId().toString())
                .setOccurredOn(Instant.now())
                .setDataBuilder(
                        AttachmentData.newBuilder()
                                .setOwnerId(attachmentSnapshot.getOwnerId().toString())
                                .setPostId(attachmentSnapshot.getPostId().toString())
                                .setExtension(attachmentSnapshot.getExtension().name())
                                .setFilename(attachmentSnapshot.getFilename())
                                .setFileUri(
                                        (attachmentSnapshot.getFileUri() != null) ? attachmentSnapshot.getFileUri().toString() : null
                                )
                                .setMd5(attachmentSnapshot.getMd5())
                                .setCreateDate(
                                        (attachmentSnapshot.getCreateDate() != null) ?
                                                attachmentSnapshot.getCreateDate().toInstant() :
                                                null
                                )
                ).build();
    }

    /**
     * Create AttachmentEvent with REMOVE state.
     *
     * @param attachmentId AttachmentId the delete event will regard.
     * @return Created attachment event.
     */
    public static AttachmentEvent deleteAttachmentEvent(final UUID postId, final UUID attachmentId) {
        return AttachmentEvent.newBuilder()
                .setState(DomainEventPublisher.AttachmentEventType.REMOVED.name())
                .setAttachmentId(attachmentId.toString())
                .setDataBuilder(
                        AttachmentData.newBuilder()
                                .setPostId(postId.toString())
                )
                .setOccurredOn(Instant.now())
                .build();
    }

}
