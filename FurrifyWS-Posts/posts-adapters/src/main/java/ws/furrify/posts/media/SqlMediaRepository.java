package ws.furrify.posts.media;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.posts.media.dto.query.MediaDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlMediaRepository extends Repository<MediaSnapshot, Long> {
    MediaSnapshot save(MediaSnapshot mediaSnapshot);

    void deleteByMediaId(UUID mediaId);

    Optional<MediaSnapshot> findByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID postId, UUID mediaId);

    boolean existsByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID postId, UUID mediaId);

    @Query("select count(a) from MediaSnapshot a where a.ownerId = ?1")
    long countMediaByUserId(UUID userId);

    Optional<MediaSnapshot> findByOwnerIdAndPostIdAndMd5(UUID ownerId, UUID postId, String md5);

}

@Transactional(rollbackFor = {})
interface SqlMediaQueryRepositoryImpl extends MediaQueryRepository, Repository<MediaSnapshot, Long> {

    @Override
    Optional<MediaDetailsQueryDTO> findByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID artistId, UUID mediaId);

    @Override
    @Query("select media from MediaSnapshot media where ownerId = ?1 and postId = ?2 order by priority desc")
    Page<MediaDetailsQueryDTO> findAllByOwnerIdAndPostId(UUID ownerId, UUID postId, Pageable pageable);

    @Override
    @Query("select id from MediaSnapshot where mediaId = ?1")
    Long getIdByMediaId(UUID mediaId);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MediaRepositoryImpl implements MediaRepository {

    private final SqlMediaRepository sqlMediaRepository;

    @Override
    public Media save(final Media media) {
        return Media.restore(
                sqlMediaRepository.save(media.getSnapshot())
        );
    }

    @Override
    public long countMediaByUserId(final UUID userId) {
        return sqlMediaRepository.countMediaByUserId(userId);
    }

    @Override
    public Optional<Media> findByOwnerIdAndPostIdAndMd5(final UUID ownerId, final UUID postId, final String md5) {
        return sqlMediaRepository.findByOwnerIdAndPostIdAndMd5(ownerId, postId, md5)
                .map(Media::restore);
    }

    @Override
    public Optional<Media> findByOwnerIdAndPostIdAndMediaId(final UUID ownerId, final UUID postId, final UUID mediaId) {
        return sqlMediaRepository.findByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId)
                .map(Media::restore);
    }

    @Override
    public void deleteByMediaId(final UUID mediaId) {
        sqlMediaRepository.deleteByMediaId(mediaId);
    }

    @Override
    public boolean existsByOwnerIdAndPostIdAndMediaId(final UUID ownerId, final UUID postId, final UUID mediaId) {
        return sqlMediaRepository.existsByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId);
    }
}