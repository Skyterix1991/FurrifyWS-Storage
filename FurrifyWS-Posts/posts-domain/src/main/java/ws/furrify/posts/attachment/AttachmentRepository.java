package ws.furrify.posts.attachment;

import java.util.Optional;
import java.util.UUID;

interface AttachmentRepository {
    Optional<Attachment> findByOwnerIdAndPostIdAndMd5(UUID ownerId, UUID postId, String md5);

    Optional<Attachment> findByOwnerIdAndPostIdAndAttachmentId(UUID ownerId, UUID postId, UUID attachmentId);

    void deleteByAttachmentId(UUID attachmentId);

    boolean existsByOwnerIdAndPostIdAndAttachmentId(UUID ownerId, UUID postId, UUID attachmentId);

    Attachment save(Attachment attachment);

    long countAttachmentsByUserId(UUID userId);
}
