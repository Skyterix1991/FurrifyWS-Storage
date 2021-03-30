package ws.furrify.posts.media.dto.query;

import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.media.MediaStatus;

import java.io.Serializable;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author Skyte
 */
public interface MediaDetailsQueryDTO extends Serializable {

    UUID getMediaId();

    UUID getPostId();

    UUID getOwnerId();

    Integer getPriority();

    String getFilename();

    MediaExtension getExtension();

    URL getFileUrl();

    URL getThumbnailUrl();

    String getFileHash();

    MediaStatus getStatus();

    ZonedDateTime getCreateDate();
}
