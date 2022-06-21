package ws.furrify.posts.attachment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.posts.attachment.dto.query.AttachmentDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlAttachmentRepository extends Repository<AttachmentSnapshot, Long> {
    AttachmentSnapshot save(AttachmentSnapshot attachmentSnapshot);

    void deleteByAttachmentId(UUID attachmentId);

    Optional<AttachmentSnapshot> findByOwnerIdAndPostIdAndAttachmentId(UUID ownerId, UUID postId, UUID attachmentId);

    boolean existsByOwnerIdAndPostIdAndAttachmentId(UUID ownerId, UUID postId, UUID attachmentId);

    @Query("select count(a) from AttachmentSnapshot a where a.ownerId = ?1")
    long countAttachmentsByUserId(UUID userId);

    Optional<AttachmentSnapshot> findByOwnerIdAndPostIdAndMd5(UUID ownerId, UUID postId, String md5);
}

@Transactional(rollbackFor = {})
interface SqlAttachmentQueryRepositoryImpl extends AttachmentQueryRepository, Repository<AttachmentSnapshot, Long> {

    @Override
    Optional<AttachmentDetailsQueryDTO> findByOwnerIdAndPostIdAndAttachmentId(UUID ownerId, UUID artistId, UUID attachmentId);

    @Override
    @Query("select attachment from AttachmentSnapshot attachment where attachment.ownerId = ?1 and attachment.postId = ?2 order by attachment.createDate desc")
    Page<AttachmentDetailsQueryDTO> findAllByOwnerIdAndPostId(UUID ownerId, UUID postId, Pageable pageable);

    @Override
    @Query("select id from AttachmentSnapshot where attachmentId = ?1")
    Long getIdByAttachmentId(UUID attachmentId);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class AttachmentRepositoryImpl implements AttachmentRepository {

    private final SqlAttachmentRepository sqlAttachmentRepository;

    @Override
    public Attachment save(final Attachment attachment) {
        return Attachment.restore(
                sqlAttachmentRepository.save(attachment.getSnapshot())
        );
    }

    @Override
    public long countAttachmentsByUserId(final UUID userId) {
        return sqlAttachmentRepository.countAttachmentsByUserId(userId);
    }

    @Override
    public Optional<Attachment> findByOwnerIdAndPostIdAndMd5(final UUID ownerId, final UUID postId, final String md5) {
        return sqlAttachmentRepository.findByOwnerIdAndPostIdAndMd5(ownerId, postId, md5)
                .map(Attachment::restore);
    }

    @Override
    public Optional<Attachment> findByOwnerIdAndPostIdAndAttachmentId(final UUID ownerId, final UUID postId, final UUID attachmentId) {
        return sqlAttachmentRepository.findByOwnerIdAndPostIdAndAttachmentId(ownerId, postId, attachmentId)
                .map(Attachment::restore);
    }

    @Override
    public void deleteByAttachmentId(final UUID attachmentId) {
        sqlAttachmentRepository.deleteByAttachmentId(attachmentId);
    }

    @Override
    public boolean existsByOwnerIdAndPostIdAndAttachmentId(final UUID ownerId, final UUID postId, final UUID attachmentId) {
        return sqlAttachmentRepository.existsByOwnerIdAndPostIdAndAttachmentId(ownerId, postId, attachmentId);
    }
}