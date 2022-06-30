package ws.furrify.sources.source.dto;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import ws.furrify.shared.vo.SourceOriginType;
import ws.furrify.sources.source.strategy.SourceStrategy;
import ws.furrify.sources.source.vo.RemoteContent;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author Skyte
 */
@Builder(toBuilder = true)
@Value
@ToString
public class SourceDTO {
    Long id;

    UUID originId;
    UUID postId;
    UUID sourceId;
    UUID ownerId;

    List<RemoteContent> remoteContentList;

    HashMap<String, String> data;

    SourceStrategy strategy;

    SourceOriginType originType;

    ZonedDateTime createDate;
}
