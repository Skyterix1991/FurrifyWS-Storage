package ws.furrify.posts.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import ws.furrify.posts.DomainEventPublisher;
import ws.furrify.posts.PostEvent;
import ws.furrify.posts.TagEvent;
import ws.furrify.posts.post.dto.PostDTO;
import ws.furrify.posts.post.dto.PostDtoFactory;

import java.util.UUID;

/**
 * @author Skyte
 */
@RequiredArgsConstructor
@Log
public class PostFacade {

    private final CreatePostPort createPostAdapter;
    private final DeletePostPort deletePostAdapter;
    private final UpdatePostDetailsPort updatePostAdapter;
    private final ReplacePostDetailsPort replacePostAdapter;
    private final PostRepository postRepository;
    private final PostFactory postFactory;
    private final PostDtoFactory postDTOFactory;

    /**
     * Handle incoming post events.
     *
     * @param postEvent Post event instance received from kafka.
     */
    public void handleEvent(final UUID key, final PostEvent postEvent) {
        PostDTO postDTO = postDTOFactory.from(key, postEvent);

        switch (DomainEventPublisher.PostEventType.valueOf(postEvent.getState())) {
            case CREATED, REPLACED, UPDATED -> savePost(postDTO);

            case REMOVED -> deletePostByPostId(postDTO.getPostId());

            default -> log.warning("State received from kafka is not defined. State=" + postEvent.getState());
        }
    }

    /**
     * Handle incoming tag events.
     *
     * @param tagEvent Tag event instance received from kafka.
     */
    public void handleEvent(final UUID key, final TagEvent tagEvent) {
        switch (DomainEventPublisher.TagEventType.valueOf(tagEvent.getState())) {
            case REMOVED -> deleteTagFromPosts(key, tagEvent.getTagValue());
            case UPDATED, REPLACED -> updateTagDetailsInPosts(key,
                    tagEvent.getTagValue(),
                    tagEvent.getData().getValue(),
                    tagEvent.getData().getType());

            default -> log.warning("State received from kafka is not defined. State=" + tagEvent.getState());
        }
    }

    /**
     * Creates post.
     *
     * @param userId  User uuid to assign post to.
     * @param postDTO Post to create.
     * @return Created post UUID.
     */
    public UUID createPost(final UUID userId, final PostDTO postDTO) {
        return createPostAdapter.createPost(userId, postDTO);
    }

    /**
     * Deletes post.
     *
     * @param postId Post UUID.
     */
    public void deletePost(final UUID userId, final UUID postId) {
        deletePostAdapter.deletePost(userId, postId);
    }

    /**
     * Replaces all fields in post.
     *
     * @param postId  Post UUID
     * @param postDTO Replacement post.
     */
    public void replacePostDetails(final UUID userId, final UUID postId, final PostDTO postDTO) {
        replacePostAdapter.replacePostDetails(userId, postId, postDTO);
    }

    /**
     * Updates specified fields in post.
     *
     * @param postId  Post UUID.
     * @param postDTO Post with updated specific fields.
     */
    public void updatePostDetails(final UUID userId, final UUID postId, final PostDTO postDTO) {
        updatePostAdapter.updatePostDetails(userId, postId, postDTO);
    }

    private void savePost(final PostDTO postDTO) {
        postRepository.save(postFactory.from(postDTO));
    }

    private void deletePostByPostId(final UUID postId) {
        postRepository.deleteByPostId(postId);
    }

    private void updateTagDetailsInPosts(final UUID ownerId,
                                         final String originalValue,
                                         final String newValue,
                                         final String newType) {
        // Get all posts with tag value, change value and save.
        postRepository.findAllByOwnerIdAndValueInTags(ownerId, originalValue).stream()
                .peek(post -> post.updateTagDetailsInTags(originalValue, newValue, newType))
                .forEach(postRepository::save);
    }

    private void deleteTagFromPosts(final UUID ownerId,
                                    final String value) {
        // Get all posts with tag value, remove it and save.
        postRepository.findAllByOwnerIdAndValueInTags(ownerId, value).stream()
                .peek(post -> post.removeTag(value))
                .forEach(postRepository::save);
    }
}
