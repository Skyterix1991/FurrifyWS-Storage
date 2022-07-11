package ws.furrify.sources.source;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import ws.furrify.sources.vo.RemoteContent;

import java.util.List;
import java.util.UUID;

@Transactional(rollbackFor = {})
interface SqlSourceRemoteContentQueryRepositoryImpl extends SourceRemoteContentQueryRepository, Repository<RemoteContent, Long> {

    @Override
    @Query("select s.remoteContentList from SourceSnapshot s where s.ownerId = ?1 and s.sourceId = ?2")
    List<RemoteContent> findAllByOwnerIdAndSourceId(UUID ownerId, UUID sourceId);
}