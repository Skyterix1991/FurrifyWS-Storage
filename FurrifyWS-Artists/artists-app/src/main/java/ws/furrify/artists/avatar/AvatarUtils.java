package ws.furrify.artists.avatar;

import ws.furrify.posts.avatar.AvatarEvent;
import ws.furrify.posts.avatar.vo.AvatarData;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.time.Instant;
import java.util.UUID;

/**
 * Utils class regarding Avatar entity.
 *
 * @author Skyte
 */
class AvatarUtils {

    /**
     * Create AvatarEvent by given aggregate and set its state change.
     *
     * @param eventType Event state change type.
     * @param avatar    Avatar aggregate to build post event from.
     * @return Created avatar event.
     */
    public static AvatarEvent createAvatarEvent(final DomainEventPublisher.AvatarEventType eventType,
                                                final Avatar avatar) {
        AvatarSnapshot avatarSnapshot = avatar.getSnapshot();

        return AvatarEvent.newBuilder()
                .setState(eventType.name())
                .setAvatarId(avatarSnapshot.getAvatarId().toString())
                .setOccurredOn(Instant.now())
                .setDataBuilder(
                        AvatarData.newBuilder()
                                .setOwnerId(avatarSnapshot.getOwnerId().toString())
                                .setArtistId(avatarSnapshot.getArtistId().toString())
                                .setExtension(avatarSnapshot.getExtension().name())
                                .setFilename(avatarSnapshot.getFilename())
                                .setFileUri(
                                        (avatarSnapshot.getFileUri() != null) ? avatarSnapshot.getFileUri().toString() : null
                                )
                                .setMd5(avatarSnapshot.getMd5())
                                .setThumbnailUri(
                                        (avatarSnapshot.getThumbnailUri() != null) ? avatarSnapshot.getThumbnailUri().toString() : null
                                )
                                .setCreateDate(
                                        (avatarSnapshot.getCreateDate() != null) ?
                                                avatarSnapshot.getCreateDate().toInstant() :
                                                null
                                )
                ).build();
    }

    /**
     * Create AvatarEvent with REMOVE state.
     *
     * @param avatarId AvatarId the delete event will regard.
     * @return Created avatar event.
     */
    public static AvatarEvent deleteAvatarEvent(final UUID artistId, final UUID avatarId) {
        return AvatarEvent.newBuilder()
                .setState(DomainEventPublisher.ArtistEventType.REMOVED.name())
                .setAvatarId(avatarId.toString())
                .setDataBuilder(
                        AvatarData.newBuilder()
                                .setArtistId(artistId.toString())
                )
                .setOccurredOn(Instant.now())
                .build();
    }

}
