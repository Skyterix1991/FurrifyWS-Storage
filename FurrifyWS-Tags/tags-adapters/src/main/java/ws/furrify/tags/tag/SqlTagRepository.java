package ws.furrify.tags.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.tags.tag.dto.query.TagDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlTagRepository extends Repository<TagSnapshot, Long> {
    TagSnapshot save(TagSnapshot tagSnapshot);

    boolean existsByOwnerIdAndValue(UUID ownerId, String value);

    @Query("from TagSnapshot t where t.ownerId = ?1 and lower(t.value) = lower(?2)")
    Optional<TagSnapshot> findByOwnerIdAndValue(UUID ownerId, String value);

    void deleteByValue(String value);

    @Query("select count(a) from TagSnapshot a where a.ownerId = ?1")
    long countTagsByUserId(UUID userId);
}

@Transactional(rollbackFor = {})
interface SqlTagQueryRepositoryImpl extends TagQueryRepository, Repository<TagSnapshot, Long> {

    @Override
    Optional<TagDetailsQueryDTO> findByOwnerIdAndValue(UUID userId, String value);

    @Override
    @Query("select tag from TagSnapshot tag where tag.ownerId = ?1 and " +
            "(?2 is null or tag.value like %?2%) order by tag.value desc")
    Page<TagDetailsQueryDTO> findAllByOwnerIdAndLikeMatch(UUID userId, String match, Pageable pageable);

    @Override
    @Query("select id from TagSnapshot where value = ?1")
    Long getIdByValue(String value);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class TagRepositoryImpl implements TagRepository {

    private final SqlTagRepository sqlTagRepository;

    @Override
    public Tag save(final Tag tag) {
        return Tag.restore(sqlTagRepository.save(tag.getSnapshot()));
    }

    @Override
    public void deleteByValue(final String value) {
        sqlTagRepository.deleteByValue(value);
    }

    @Override
    public boolean existsByOwnerIdAndValue(final UUID ownerId, final String value) {
        return sqlTagRepository.existsByOwnerIdAndValue(ownerId, value);
    }

    @Override
    public Optional<Tag> findByOwnerIdAndValue(final UUID userId, final String value) {
        return sqlTagRepository.findByOwnerIdAndValue(userId, value).map(Tag::restore);
    }

    @Override
    public long countTagsByUserId(final UUID userId) {
        return sqlTagRepository.countTagsByUserId(userId);
    }
}