package ws.furrify.sources.source;

import ws.furrify.sources.vo.RemoteContent;

import java.util.List;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface SourceRemoteContentQueryRepository {

    List<RemoteContent> findAllByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);
}
