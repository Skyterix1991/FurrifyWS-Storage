package ws.furrify.sources.source;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ws.furrify.sources.source.dto.query.SourceDetailsQueryDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface SourceQueryRepository {
    Optional<SourceDetailsQueryDTO> findByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);

    Page<SourceDetailsQueryDTO> findAllByOwnerId(UUID ownerId, Pageable pageable);

    Long getIdBySourceId(UUID sourceId);

    List<SourceDetailsQueryDTO> findAllByOwnerIdAndArtistId(UUID userId, UUID artistId);

    Page<SourceDetailsQueryDTO> findAllByOwnerIdAndArtistIdAndPageable(UUID userId, UUID artistId, Pageable pageable);

    Page<SourceDetailsQueryDTO> findAllByOwnerIdAndPostIdAndMediaId(UUID userId, UUID postId, UUID mediaId, Pageable pageable);

    Page<SourceDetailsQueryDTO> findAllByOwnerIdAndPostIdAndAttachmentId(UUID userId, UUID postId, UUID attachmentId, Pageable pageable);

    Long countByOwnerIdAndOriginId(UUID ownerId, UUID originId);
}
