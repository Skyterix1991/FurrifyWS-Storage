package ws.furrify.sources.source;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.sources.source.dto.query.SourceDetailsQueryDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlSourceRepository extends Repository<SourceSnapshot, Long> {
    SourceSnapshot save(SourceSnapshot sourceSnapshot);

    void deleteBySourceId(UUID sourceId);

    boolean existsByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);

    Optional<SourceSnapshot> findByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);

    @Query("select count(a) from SourceSnapshot a where a.ownerId = ?1")
    long countSourcesByUserId(UUID userId);
}

@Transactional(rollbackFor = {})
interface SqlSourceQueryRepositoryImpl extends SourceQueryRepository, Repository<SourceSnapshot, Long> {

    @Override
    Optional<SourceDetailsQueryDTO> findByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);

    @Override
    Page<SourceDetailsQueryDTO> findAllByOwnerId(UUID ownerId, Pageable pageable);

    @Override
    @Query("select id from SourceSnapshot where sourceId = ?1")
    Long getIdBySourceId(UUID sourceId);

    @Override
    @Query("select s from SourceSnapshot s where s.ownerId = ?1 and s.originType = 'ARTIST' and s.originId = ?2")
    Page<SourceDetailsQueryDTO> findAllByOwnerIdAndArtistIdAndPageable(UUID userId, UUID artistId, Pageable pageable);

    @Override
    @Query("select s from SourceSnapshot s where s.ownerId = ?1 and s.postId = ?2 and s.originType = 'MEDIA' and s.originId = ?3")
    Page<SourceDetailsQueryDTO> findAllByOwnerIdAndPostIdAndMediaId(UUID userId, UUID postId, UUID mediaId, Pageable pageable);

    @Override
    @Query("select s from SourceSnapshot s where s.ownerId = ?1 and s.postId = ?2  and s.originType = 'ATTACHMENT' and s.originId = ?3")
    Page<SourceDetailsQueryDTO> findAllByOwnerIdAndPostIdAndAttachmentId(UUID userId, UUID postId, UUID attachmentId, Pageable pageable);

    @Override
    @Query("select count(source) from SourceSnapshot source where source.ownerId = ?1 " +
            "and source.originId = ?2")
    Long countByOwnerIdAndOriginId(UUID ownerId, UUID originId);

    @Override
    @Query("select s from SourceSnapshot s where s.ownerId = ?1 and s.originType = 'ARTIST' and s.originId = ?2")
    List<SourceDetailsQueryDTO> findAllByOwnerIdAndArtistId(UUID userId, UUID artistId);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class SourceRepositoryImpl implements SourceRepository {

    private final SqlSourceRepository sqlSourceRepository;

    @Override
    public Source save(final Source post) {
        return Source.restore(sqlSourceRepository.save(post.getSnapshot()));
    }

    @Override
    public void deleteBySourceId(final UUID sourceId) {
        sqlSourceRepository.deleteBySourceId(sourceId);
    }

    @Override
    public Optional<Source> findByOwnerIdAndSourceId(final UUID ownerId, final UUID sourceId) {
        return sqlSourceRepository.findByOwnerIdAndSourceId(ownerId, sourceId).map(Source::restore);
    }

    @Override
    public boolean existsByOwnerIdAndSourceId(final UUID ownerId, final UUID sourceId) {
        return sqlSourceRepository.existsByOwnerIdAndSourceId(ownerId, sourceId);
    }

    @Override
    public long countSourcesByUserId(final UUID userId) {
        return sqlSourceRepository.countSourcesByUserId(userId);
    }


}