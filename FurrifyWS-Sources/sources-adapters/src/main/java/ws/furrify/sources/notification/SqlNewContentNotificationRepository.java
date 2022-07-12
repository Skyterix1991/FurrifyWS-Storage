package ws.furrify.sources.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.sources.notification.dto.query.NewContentNotificationDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlNewContentNotificationRepository extends Repository<NewContentNotificationSnapshot, Long> {
    NewContentNotificationSnapshot save(NewContentNotificationSnapshot newContentNotificationSnapshot);

    Optional<NewContentNotificationSnapshot> findByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);
}

@Transactional(rollbackFor = {})
interface SqlNewContentNotificationQueryRepositoryImpl extends NewContentNotificationQueryRepository, Repository<NewContentNotificationSnapshot, Long> {

    @Override
    @Query("select n.id from NewContentNotificationSnapshot n where n.notificationId = ?1")
    Long getIdByNotificationId(UUID notificationId);


    @Override
    @Query("select n from NewContentNotificationSnapshot n where n.ownerId = ?1 and n.sourceId = ?2 and n.notificationId = ?3")
    Optional<NewContentNotificationDetailsQueryDTO> findByOwnerIdAndSourceIdAndNotificationId(UUID userId, UUID sourceId, UUID notificationId);

    @Override
    @Query("select n from NewContentNotificationSnapshot n where n.ownerId = ?1")
    Page<NewContentNotificationDetailsQueryDTO> findAllByOwnerId(UUID userId, Pageable pageable);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class NewContentNotificationRepositoryImpl implements NewContentNotificationRepository {

    private final SqlNewContentNotificationRepository sqlNewContentNotificationRepository;

    @Override
    public NewContentNotification save(final NewContentNotification notification) {
        return NewContentNotification.restore(sqlNewContentNotificationRepository.save(notification.getSnapshot()));
    }

    @Override
    public Optional<NewContentNotification> findByOwnerIdAndSourceId(final UUID ownerId, final UUID sourceId) {
        return sqlNewContentNotificationRepository.findByOwnerIdAndSourceId(ownerId, sourceId).map(NewContentNotification::restore);
    }
}