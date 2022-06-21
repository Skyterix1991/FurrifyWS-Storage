package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.posts.post.dto.query.PostDetailsQueryDTO;
import ws.furrify.posts.post.dto.vo.PostQuerySearchDTO;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlPostRepository extends Repository<PostSnapshot, Long> {
    PostSnapshot save(PostSnapshot postSnapshot);

    void deleteByPostId(UUID postId);

    boolean existsByOwnerIdAndPostId(UUID ownerId, UUID postId);

    Optional<PostSnapshot> findByOwnerIdAndPostId(UUID ownerId, UUID postId);

    @Query("from PostSnapshot post join post.tags tag where tag.value = ?2 and post.ownerId = ?1")
    Set<PostSnapshot> findAllByOwnerIdAndValueInTags(UUID ownerId, String value);

    @Query("from PostSnapshot post join post.artists artist where artist.artistId = ?2 and post.ownerId = ?1")
    Set<PostSnapshot> findAllByOwnerIdAndArtistIdInArtists(UUID ownerId, UUID artistId);

    @Query("from PostSnapshot post join post.mediaSet media where media.mediaId = ?3 and post.postId = ?2 and post.ownerId = ?1")
    Optional<PostSnapshot> findByOwnerIdAndPostIdAndMediaId(UUID ownerId, UUID postId, UUID mediaId);

    @Query("from PostSnapshot post join post.attachments attachment where attachment.attachmentId = ?3 and post.postId = ?2 and post.ownerId = ?1")
    Optional<PostSnapshot> findByOwnerIdAndPostIdAndAttachmentId(UUID ownerId, UUID postId, UUID attachmentId);

    @Query("select count(a) from PostSnapshot a where a.ownerId = ?1")
    long countPostsByUserId(UUID userId);
}

@Transactional(rollbackFor = {})
interface SqlPostQueryRepositoryImpl extends PostQueryRepository, Repository<PostSnapshot, Long> {

    @Override
    Optional<PostDetailsQueryDTO> findByOwnerIdAndPostId(UUID ownerId, UUID postId);

    @Override
    Page<PostDetailsQueryDTO> findAllByOwnerId(UUID ownerId, Pageable pageable);

    @Override
    @Query("select p from PostSnapshot p where " +
            // Tags
            "(:#{#query.withTags.size()} = 0 or p.id in (" +
            "select post.id from PostSnapshot post inner join post.tags tag " +
            "where tag.value in (:#{#query.withTags}) " +
            "group by post.id " +
            "having count(distinct tag.value) = :#{#query.withTags.size() * 1L}" +
            ")) and (:#{#query.withoutTags.size()} = 0 or p.id not in (" +
            "select post.id from PostSnapshot post inner join post.tags tag " +
            "where tag.value in (:#{#query.withoutTags}) " +
            "group by post.id " +
            "having count(distinct tag.value) = :#{#query.withoutTags.size() * 1L}" +
            ")) " +
            // Artists
            "and (:#{#query.withArtists.size()} = 0 or p.id in (" +
            "select post.id from PostSnapshot post inner join post.artists artist " +
            "where lower(artist.preferredNickname) in (:#{#query.withArtists}) " +
            "group by post.id " +
            "having count(distinct artist.preferredNickname) = :#{#query.withArtists.size() * 1L}" +
            ")) and (:#{#query.withoutArtists.size()} = 0 or p.id not in (" +
            "select post.id from PostSnapshot post inner join post.artists artist " +
            "where lower(artist.preferredNickname) in (:#{#query.withoutArtists}) " +
            "group by post.id " +
            "having count(distinct artist.preferredNickname) = :#{#query.withoutArtists.size() * 1L}" +
            ")) " +
            // Title like words
            "and lower(p.title) like concat('%', lower(:#{(#query.words.isEmpty()) ? '' : #query.words.toArray()[0]}) ,'%')" +
            // OwnerId
            "and p.ownerId = :ownerId")
    Page<PostSnapshot> findAllByOwnerIdAndQuery(@Param("ownerId") UUID ownerId, @Param("query") PostQuerySearchDTO query, Pageable pageable);

    @Override
    @Query("select post from PostSnapshot post join post.artists artist where artist.artistId = ?2 and post.ownerId = ?1")
    Page<PostDetailsQueryDTO> findAllByOwnerIdAndArtistId(UUID ownerId, UUID artistId, Pageable pageable);

    @Override
    @Query("select id from PostSnapshot where postId = ?1")
    Long getIdByPostId(UUID postId);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PostRepositoryImpl implements PostRepository {

    private final SqlPostRepository sqlPostRepository;

    @Override
    public Post save(final Post post) {
        return Post.restore(sqlPostRepository.save(post.getSnapshot()));
    }

    @Override
    public Set<Post> findAllByOwnerIdAndValueInTags(final UUID ownerId, final String value) {
        return sqlPostRepository.findAllByOwnerIdAndValueInTags(ownerId, value)
                .stream()
                .map(Post::restore)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean existsByOwnerIdAndPostId(final UUID ownerId, final UUID postId) {
        return sqlPostRepository.existsByOwnerIdAndPostId(ownerId, postId);
    }

    @Override
    public Optional<Post> findByOwnerIdAndPostId(final UUID ownerId, final UUID postId) {
        return sqlPostRepository.findByOwnerIdAndPostId(ownerId, postId).map(Post::restore);
    }

    @Override
    public void deleteByPostId(final UUID postId) {
        sqlPostRepository.deleteByPostId(postId);
    }

    @Override
    public Set<Post> findAllByOwnerIdAndArtistIdInArtists(final UUID ownerId, final UUID artistId) {
        return sqlPostRepository.findAllByOwnerIdAndArtistIdInArtists(ownerId, artistId)
                .stream()
                .map(Post::restore)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<Post> findByOwnerIdAndPostIdAndMediaId(final UUID ownerId, final UUID postId, final UUID mediaId) {
        return sqlPostRepository.findByOwnerIdAndPostIdAndMediaId(ownerId, postId, mediaId).map(Post::restore);
    }

    @Override
    public Optional<Post> findByOwnerIdAndPostIdAndAttachmentId(final UUID ownerId, final UUID postId, final UUID attachment) {
        return sqlPostRepository.findByOwnerIdAndPostIdAndAttachmentId(ownerId, postId, attachment).map(Post::restore);
    }

    @Override
    public long countPostsByUserId(final UUID userId) {
        return sqlPostRepository.countPostsByUserId(userId);
    }
}