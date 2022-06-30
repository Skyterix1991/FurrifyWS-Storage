package ws.furrify.posts.post;

import ws.furrify.posts.artist.ArtistServiceClient;
import ws.furrify.posts.artist.dto.query.ArtistDetailsQueryDTO;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostArtistData;
import ws.furrify.posts.post.vo.PostData;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.post.vo.PostTagData;
import ws.furrify.posts.tag.TagServiceClient;
import ws.furrify.posts.tag.dto.query.TagDetailsQueryDTO;
import ws.furrify.shared.exception.Errors;
import ws.furrify.shared.exception.RecordNotFoundException;
import ws.furrify.shared.kafka.DomainEventPublisher;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utils class regarding Post entity.
 *
 * @author Skyte
 */
class PostUtils {

    /**
     * Coverts PostTag with value to PostTag with value and type.
     *
     * @param ownerId          Owner UUID.
     * @param tags             Tags with values to convert.
     * @param tagServiceClient Tag Service Client instance.
     * @return Tags with value and type.
     */
    public static Set<PostTag> tagValueToTagVO(UUID ownerId,
                                               Set<PostTag> tags,
                                               TagServiceClient tagServiceClient) {
        return tags.stream()
                .map(tagWithValue -> {
                            TagDetailsQueryDTO tag = Optional.ofNullable(
                                    tagServiceClient.getUserTag(ownerId, tagWithValue.getValue())
                            ).orElseThrow(() -> new RecordNotFoundException(Errors.NO_TAG_FOUND.getErrorMessage(tagWithValue.getValue())));

                            return new PostTag(tag.getValue(), tag.getType());
                        }
                ).collect(Collectors.toSet());
    }

    /**
     * Coverts PostArtist with artistId to PostArtist with preferred nickname.
     *
     * @param ownerId             Owner UUID.
     * @param artists             Artists with artistIds to convert.
     * @param artistServiceClient Artist Service Client instance.
     * @return Artists with artistIds and preferred nicknames.
     */
    public static Set<PostArtist> artistWithArtistIdToArtistVO(UUID ownerId,
                                                               Set<PostArtist> artists,
                                                               ArtistServiceClient artistServiceClient) {
        return artists.stream()
                .map(oldArtist -> {
                            ArtistDetailsQueryDTO artist = Optional.ofNullable(
                                    artistServiceClient.getUserArtist(ownerId, oldArtist.getArtistId())
                            ).orElseThrow(() -> new RecordNotFoundException(Errors.NO_RECORD_FOUND.getErrorMessage(oldArtist.getArtistId())));

                            final ArtistDetailsQueryDTO.ArtistAvatar avatar = artist.getAvatar();
                            // Check if avatar exists
                            URI thumbnailUri = (avatar == null) ? null : avatar.getThumbnailUri();

                            return new PostArtist(artist.getArtistId(), artist.getPreferredNickname(), thumbnailUri);
                        }
                ).collect(Collectors.toSet());
    }

    /**
     * Create PostEvent by given aggregate and set its state change.
     *
     * @param eventType Event state change type.
     * @param post      Post aggregate to build post event from.
     * @return Created post event.
     */
    public static PostEvent createPostEvent(final DomainEventPublisher.PostEventType eventType,
                                            final Post post) {
        PostSnapshot postSnapshot = post.getSnapshot();

        return PostEvent.newBuilder()
                .setState(eventType.name())
                .setPostId(postSnapshot.getPostId().toString())
                .setOccurredOn(Instant.now())
                .setDataBuilder(
                        PostData.newBuilder()
                                .setOwnerId(postSnapshot.getOwnerId().toString())
                                .setTitle(postSnapshot.getTitle())
                                .setDescription(postSnapshot.getDescription())
                                .setTags(
                                        // Map PostTag to PostTagData
                                        postSnapshot.getTags().stream()
                                                .map(tag ->
                                                        PostTagData.newBuilder()
                                                                .setValue(tag.getValue())
                                                                .setType(tag.getType())
                                                                .build()
                                                ).collect(Collectors.toList())
                                )
                                .setArtists(
                                        // Map PostArtist to PostArtistData
                                        postSnapshot.getArtists().stream()
                                                .map(artist ->
                                                        PostArtistData.newBuilder()
                                                                .setArtistId(artist.getArtistId().toString())
                                                                .setPreferredNickname(artist.getPreferredNickname())
                                                                .setThumbnailUri(
                                                                        (artist.getThumbnailUri() != null) ? artist.getThumbnailUri().toString() : null
                                                                )
                                                                .build()
                                                ).collect(Collectors.toList())
                                )
                                .setCreateDate(
                                        (postSnapshot.getCreateDate() != null) ?
                                                postSnapshot.getCreateDate().toInstant() :
                                                null
                                )
                ).build();
    }

    /**
     * Create PostEvent with REMOVE state.
     *
     * @param postId PostId the delete event will regard.
     * @return Created post event.
     */
    public static PostEvent deletePostEvent(final UUID postId) {
        return PostEvent.newBuilder()
                .setState(DomainEventPublisher.PostEventType.REMOVED.name())
                .setPostId(postId.toString())
                .setDataBuilder(PostData.newBuilder())
                .setOccurredOn(Instant.now())
                .build();
    }

}
