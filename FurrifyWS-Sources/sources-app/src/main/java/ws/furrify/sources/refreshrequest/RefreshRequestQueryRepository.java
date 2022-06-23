package ws.furrify.sources.refreshrequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ws.furrify.sources.refreshrequest.dto.query.RefreshRequestDetailsQueryDTO;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface RefreshRequestQueryRepository {
    Long getIdByRefreshRequestId(UUID sourceId);

    Page<RefreshRequestDetailsQueryDTO> findAllByOwnerId(UUID ownerId, Pageable pageable);

    Optional<RefreshRequestDetailsQueryDTO> findByOwnerIdAndRefreshRequestId(UUID ownerId, UUID refreshRequestId);
}
