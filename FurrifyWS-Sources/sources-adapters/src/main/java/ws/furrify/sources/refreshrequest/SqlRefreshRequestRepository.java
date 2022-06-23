package ws.furrify.sources.refreshrequest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.sources.refreshrequest.dto.query.RefreshRequestDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

@Transactional(rollbackFor = RuntimeException.class)
interface SqlRefreshRequestRepository extends Repository<RefreshRequestSnapshot, Long> {
    RefreshRequestSnapshot save(RefreshRequestSnapshot refreshRequestSnapshot);
}

@Transactional(rollbackFor = {})
interface SqlRefreshRequestQueryRepositoryImpl extends RefreshRequestQueryRepository, Repository<RefreshRequestSnapshot, Long> {

    @Override
    @Query("select id from RefreshRequestSnapshot where refreshRequestId = ?1")
    Long getIdByRefreshRequestId(UUID refreshRequestId);

    @Override
    Page<RefreshRequestDetailsQueryDTO> findAllByOwnerId(UUID ownerId, Pageable pageable);

    @Override
    Optional<RefreshRequestDetailsQueryDTO> findByOwnerIdAndRefreshRequestId(UUID ownerId, UUID refreshRequestId);
}

@org.springframework.stereotype.Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RefreshRequestRepositoryImpl implements RefreshRequestRepository {

    private final SqlRefreshRequestRepository sqlRefreshRequestRepository;

    @Override
    public RefreshRequest save(final RefreshRequest refreshRequest) {
        return RefreshRequest.restore(sqlRefreshRequestRepository.save(refreshRequest.getSnapshot()));
    }
}