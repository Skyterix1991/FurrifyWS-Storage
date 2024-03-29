package ws.furrify.posts.post;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.attachment.AttachmentExtension;
import ws.furrify.posts.media.MediaExtension;
import ws.furrify.posts.post.vo.PostArtist;
import ws.furrify.posts.post.vo.PostAttachment;
import ws.furrify.posts.post.vo.PostDescription;
import ws.furrify.posts.post.vo.PostMedia;
import ws.furrify.posts.post.vo.PostTag;
import ws.furrify.posts.post.vo.PostTitle;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PostTest {

    // Mocked in beforeAll()
    private static PostRepository postRepository;

    private PostSnapshot postSnapshot;
    private Post post;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        postSnapshot = PostSnapshot.builder()
                .id(0L)
                .postId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .title("Test")
                .description("dsa")
                .tags(Collections.singleton(new PostTag("tag_value", "ACTION")))
                .artists(Collections.singleton(new PostArtist(UUID.randomUUID(), "example_nickname", new URI("/test"))))
                .mediaSet(Collections.singleton(
                        PostMedia.builder()
                                .mediaId(UUID.randomUUID())
                                .priority(1)
                                .fileUri(new URI("/test"))
                                .thumbnailUri(new URI("/test"))
                                .extension(MediaExtension.EXTENSION_PNG.name())
                                .build()
                ))
                .attachments(Collections.singleton(
                        PostAttachment.builder()
                                .attachmentId(UUID.randomUUID())
                                .fileUri(new URI("/test"))
                                .filename("yes.psd")
                                .extension(AttachmentExtension.EXTENSION_PSD.name())
                                .build()
                ))
                .createDate(ZonedDateTime.now())
                .build();

        post = Post.restore(postSnapshot);
    }

    @BeforeAll
    static void beforeAll() {
        postRepository = mock(PostRepository.class);
    }

    @Test
    @DisplayName("Post restore from snapshot")
    void restore() {
        // Given postSnapshot
        // When restore() method called
        Post post = Post.restore(postSnapshot);
        // Then restore all values into aggregate and back to snapshot without losing data
        assertEquals(postSnapshot, post.getSnapshot(), "Data was lost in restore call.");
    }

    @Test
    @DisplayName("Get snapshot from post")
    void getSnapshot() {
        // Given post
        // When getSnapshot() method called
        PostSnapshot postSnapshot = post.getSnapshot();
        // Then get snapshot of current data in aggregate
        assertEquals(this.postSnapshot, postSnapshot, "Data was lost in snapshot.");
    }

    @Test
    @DisplayName("Update title")
    void updateTitle() {
        // Given title
        String newTitle = "Title2";
        // When updateDetails() method called
        // Then update title
        post.updateTitle(
                PostTitle.of(newTitle)
        );

        assertEquals(newTitle, post.getSnapshot().getTitle(), "Title was not updated.");
    }

    @Test
    @DisplayName("Update description")
    void updateDescription() {
        // Given description
        String newDesc = "dsadasdsa2";
        // When updateDetails() method called
        // Then update description
        post.updateDescription(
                PostDescription.of(newDesc)
        );

        assertEquals(newDesc, post.getSnapshot().getDescription(), "Description was not updated.");
    }


    @Test
    @DisplayName("Remove tag")
    void removeTag() {
        // Given tag value
        String tagValue = "tag_value";
        // When removeTag() method called
        // Then remove tag from tags
        post.removeTag(tagValue);

        assertEquals(
                0,
                post.getSnapshot().getTags().size(),
                "Tag was not removed."
        );
    }

    @Test
    @DisplayName("Update tag details in tags")
    void updateTagDetailsInTags() {
        // Given existing tag value nad new tag value and new tag type
        String tagValue = "tag_value";
        String newTagValue = "new_tag_value";
        String newTagType = "BACKGROUND";
        // When updateTagDetailsInTags() method called
        // Then update tag details in tags
        post.updateTagDetailsInTags(tagValue, new PostTag(newTagValue, newTagType));

        assertEquals(
                post.getSnapshot().getTags().toArray()[0],
                new PostTag(newTagValue, newTagType),
                "Tag was not updated."
        );
    }

    @Test
    @DisplayName("Update tag details in tags with non existing tag value")
    void updateTagDetailsInTags2() {
        // Given non existing tag value nad new tag value and new tag type
        String tagValue = "non_existing_tag_value";
        String newTagValue = "new_tag_value";
        String newTagType = "BACKGROUND";
        // When updateTagDetailsInTags() method called
        // Then throw IllegalStateException

        assertThrows(
                IllegalStateException.class,
                () -> post.updateTagDetailsInTags(tagValue, new PostTag(newTagValue, newTagType)),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Replace tags")
    void replaceTags() {
        // Given new tags set
        PostTag postTag = new PostTag("tag_value", "ACTION");

        Set<PostTag> newTags = Collections.singleton(postTag);
        // When replaceTags() method called
        // Then replace tags
        post.replaceTags(newTags);

        assertEquals(
                post.getSnapshot().getTags().toArray()[0],
                postTag,
                "Tags weren't replaced."
        );
    }

    @Test
    @DisplayName("Remove artist")
    void removeArtist() {
        // Given artistId
        UUID artistId = ((PostArtist) postSnapshot.getArtists().toArray()[0]).getArtistId();
        // When removeArtist() method called
        // Then remove artist from artists
        post.removeArtist(artistId);

        assertEquals(
                0,
                post.getSnapshot().getArtists().size(),
                "Artist was not removed."
        );
    }

    @Test
    @DisplayName("Update artist details in artists")
    void updateArtistDetailsInArtists() throws URISyntaxException {
        // Given existing artistId and new artist preferredNickname
        UUID artistId = ((PostArtist) postSnapshot.getArtists().toArray()[0]).getArtistId();
        String newPreferredNickname = "example_nickname2";
        // When updateArtistDetailsInArtists() method called
        // Then update artist details in artists
        post.updateArtistDetailsInArtists(artistId, newPreferredNickname);

        assertEquals(
                post.getSnapshot().getArtists().toArray()[0],
                new PostArtist(artistId, newPreferredNickname, new URI("/test")),
                "Artist was not updated."
        );
    }

    @Test
    @DisplayName("Update artist details in artists with non existing artistId")
    void updateArtistDetailsInArtists2() {
        // Given non existing artistId and new artist preferredNickname
        UUID artistId = UUID.randomUUID();
        String newPreferredNickname = "example_nickname2";
        // When updateArtistDetailsInArtists() method called
        // Then throw IllegalStateException

        assertThrows(
                IllegalStateException.class,
                () -> post.updateArtistDetailsInArtists(artistId, newPreferredNickname),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Replace artists in post")
    void replaceArtists() throws URISyntaxException {
        // Given new artists set
        PostArtist postArtist = new PostArtist(UUID.randomUUID(), "preferred_nickname", new URI("/t2est"));

        Set<PostArtist> newArtists = Collections.singleton(postArtist);
        // When replaceArtists() method called
        // Then replace artists
        post.replaceArtists(newArtists);

        assertEquals(
                post.getSnapshot().getArtists().toArray()[0],
                postArtist,
                "Artists weren't replaced."
        );
    }

    @Test
    @DisplayName("Remove media")
    void removeMedia() {
        // Given mediaId
        UUID mediaId = ((PostMedia) postSnapshot.getMediaSet().toArray()[0]).getMediaId();
        // When removeMedia() method called
        // Then remove media from mediaSet
        post.removeMedia(mediaId);

        assertEquals(
                0,
                post.getSnapshot().getMediaSet().size(),
                "Media was not removed."
        );
    }

    @Test
    @DisplayName("Add media")
    void addMedia() throws URISyntaxException {
        // Given post media
        PostMedia postMedia = PostMedia.builder()
                .mediaId(UUID.randomUUID())
                .extension("EXTENSION_PNG")
                .priority(3)
                .thumbnailUri(new URI("/test"))
                .fileUri(new URI("/test"))
                .build();
        // When addMedia() method called
        // Then add media to set
        post.addMedia(
                postMedia
        );

        assertTrue(
                post.getSnapshot().getMediaSet().contains(postMedia),
                "Media was not added."
        );
    }

    @Test
    @DisplayName("Add attachment")
    void addAttachment() throws URISyntaxException {
        // Given post attachment
        PostAttachment postAttachment = PostAttachment.builder()
                .attachmentId(UUID.randomUUID())
                .extension("PSD")
                .filename("sad.psd")
                .fileUri(new URI("/test"))
                .build();
        // When addAttachment() method called
        // Then add attachment to set
        post.addAttachment(
                postAttachment
        );

        assertTrue(
                post.getSnapshot().getAttachments().contains(postAttachment),
                "Attachment was not added."
        );
    }

    @Test
    @DisplayName("Update media details in mediaSet")
    void updateMediaDetailsInMediaSet() throws URISyntaxException {
        // Given existing mediaId, new priority, new thumbnailUri, new extension and new status
        UUID mediaId = ((PostMedia) postSnapshot.getMediaSet().toArray()[0]).getMediaId();
        Integer newPriority = 32;
        URI newThumbnailUri = new URI("/test");
        URI newFileUri = new URI("/test");
        String newExtension = "JPEG";
        // When updateMediaDetailsInMediaSet() method called
        // Then update artist details in artists
        post.updateMediaDetailsInMediaSet(
                PostMedia.builder()
                        .mediaId(mediaId)
                        .priority(newPriority)
                        .fileUri(newFileUri)
                        .thumbnailUri(newThumbnailUri)
                        .extension(newExtension)
                        .build()
        );

        assertEquals(
                post.getSnapshot().getMediaSet().toArray()[0],
                PostMedia.builder()
                        .mediaId(mediaId)
                        .priority(newPriority)
                        .fileUri(newFileUri)
                        .thumbnailUri(newThumbnailUri)
                        .extension(newExtension)
                        .build(),
                "Media was not updated."
        );
    }

    @Test
    @DisplayName("Update media details in mediaSet with non existing mediaID")
    void updateMediaDetailsInMediaSet2() throws URISyntaxException {
        // Given non existing mediaId, new priority, new thumbnailUri, new extension and new status
        UUID mediaId = UUID.randomUUID();
        Integer newPriority = 32;
        URI newThumbnailUri = new URI("/test");
        URI newFileUri = new URI("/test");
        String newExtension = "JPEG";
        // When updateMediaDetailsInMediaSet() method called
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                () -> post.updateMediaDetailsInMediaSet(
                        PostMedia.builder()
                                .mediaId(mediaId)
                                .priority(newPriority)
                                .fileUri(newFileUri)
                                .thumbnailUri(newThumbnailUri)
                                .extension(newExtension)
                                .build()
                ),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Remove attachment")
    void removeAttachment() {
        // Given attachmentId
        UUID attachmentId = ((PostAttachment) postSnapshot.getAttachments().toArray()[0]).getAttachmentId();
        // When removeAttachment() method called
        // Then remove attachment from attachments
        post.removeAttachment(attachmentId);

        assertEquals(
                0,
                post.getSnapshot().getAttachments().size(),
                "Attachment was not removed."
        );
    }

    @Test
    @DisplayName("Update attachment details in attachments")
    void updateAttachmentDetailsInAttachmentSet() throws MalformedURLException, URISyntaxException {
        // Given existing attachmentId, new fileUri and new extension
        UUID attachmentId = ((PostAttachment) postSnapshot.getAttachments().toArray()[0]).getAttachmentId();
        URI newFileUri = new URI("/test");
        String newExtension = "PSD";
        String newFilename = "new.psd";
        // When updateAttachmentDetailsInAttachments() method called
        // Then update attachment details in attachments
        post.updateAttachmentDetailsInAttachments(
                PostAttachment.builder()
                        .attachmentId(attachmentId)
                        .filename(newFilename)
                        .fileUri(newFileUri)
                        .extension(newExtension)
                        .build()
        );

        assertEquals(
                post.getSnapshot().getAttachments().toArray()[0],
                PostAttachment.builder()
                        .attachmentId(attachmentId)
                        .filename(newFilename)
                        .fileUri(newFileUri)
                        .extension(newExtension)
                        .build(),
                "Attachment was not updated."
        );
    }

    @Test
    @DisplayName("Update attachment details in attachments with non existing attachmentId")
    void updateAttachmentDetailsInAttachmentSet2() throws URISyntaxException {
        // Given non existing attachmentId, new thumbnailUri and new extension
        UUID attachmentId = UUID.randomUUID();
        URI newFileUri = new URI("/test");
        String newExtension = "PSD";
        String newFilename = "new.psd";
        // When updateAttachmentDetailsInAttachments() method called
        // Then throw IllegalStateException

        assertThrows(
                IllegalStateException.class,
                () -> post.updateAttachmentDetailsInAttachments(
                        PostAttachment.builder()
                                .attachmentId(attachmentId)
                                .filename(newFilename)
                                .fileUri(newFileUri)
                                .extension(newExtension)
                                .build()
                ),
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Set artist thumbnail uri")
    void setArtistThumbnailUri() throws URISyntaxException {
        // Given existing artistId and new fileUri
        UUID artistId = ((PostArtist) postSnapshot.getArtists().toArray()[0]).getArtistId();
        URI newThumbnailUri = new URI("/test");
        // When setArtistThumbnailUri() method called
        // Then set artist thumbnail url
        post.setArtistThumbnailUri(
                artistId,
                newThumbnailUri
        );

        assertEquals(
                newThumbnailUri,
                ((PostArtist) post.getSnapshot().getArtists().toArray()[0]).getThumbnailUri(),
                "Artist thumbnail uri was not updated."
        );
    }
}