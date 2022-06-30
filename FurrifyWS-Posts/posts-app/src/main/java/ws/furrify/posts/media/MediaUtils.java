package ws.furrify.posts.media;

import ws.furrify.posts.media.vo.MediaData;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.Instant;
import java.util.UUID;

/**
 * Utils class regarding Media entity.
 *
 * @author Skyte
 */
class MediaUtils {

    /**
     * Create MediaEvent by given aggregate and set its state change.
     *
     * @param eventType Event state change type.
     * @param media     Media aggregate to build post event from.
     * @return Created media event.
     */
    public static MediaEvent createMediaEvent(final DomainEventPublisher.MediaEventType eventType,
                                              final Media media) {
        MediaSnapshot mediaSnapshot = media.getSnapshot();

        return MediaEvent.newBuilder()
                .setState(eventType.name())
                .setMediaId(mediaSnapshot.getMediaId().toString())
                .setOccurredOn(Instant.now())
                .setDataBuilder(
                        MediaData.newBuilder()
                                .setOwnerId(mediaSnapshot.getOwnerId().toString())
                                .setPostId(mediaSnapshot.getPostId().toString())
                                .setPriority(mediaSnapshot.getPriority())
                                .setExtension(mediaSnapshot.getExtension().name())
                                .setFilename(mediaSnapshot.getFilename())
                                .setFileUri(
                                        (mediaSnapshot.getFileUri() != null) ? mediaSnapshot.getFileUri().toString() : null
                                )
                                .setMd5(mediaSnapshot.getMd5())
                                .setThumbnailUri(
                                        (mediaSnapshot.getThumbnailUri() != null) ? mediaSnapshot.getThumbnailUri().toString() : null
                                )
                                .setCreateDate(
                                        (mediaSnapshot.getCreateDate() != null) ?
                                                mediaSnapshot.getCreateDate().toInstant() :
                                                null
                                )
                ).build();
    }

    /**
     * Create MediaEvent with REMOVE state.
     *
     * @param mediaId MediaId the delete event will regard.
     * @return Created media event.
     */
    public static MediaEvent deleteMediaEvent(final UUID postId, final UUID mediaId) {
        return MediaEvent.newBuilder()
                .setState(DomainEventPublisher.MediaEventType.REMOVED.name())
                .setMediaId(mediaId.toString())
                .setDataBuilder(
                        MediaData.newBuilder()
                                .setPostId(postId.toString())
                )
                .setOccurredOn(Instant.now())
                .build();
    }

}
